import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {catchError, filter, finalize} from 'rxjs/operators';
import {Observable, Subject} from 'rxjs';
import {ErrorService} from "../error/error.service";

export type Sender = 'user' | 'server';

export type Message = {
  chatId?: string;
  text: string;
  sender: Sender;
};

export type AgentModel = AgentRequest | AgentResponse;

export type AgentRequest = NewChatRequest | ChatRequest;

export type AgentResponse = ChatResponse

export type NewChatRequest = {
  type: "new-chat-request";
  message: string;
}

export type ChatRequest = {
  type: "chat-request";
  chatId: string;
  message: string;
}

export type ChatResponse = {
  type: "chat-response";
  chatId: string;
  message: string;
}

@Injectable({
  providedIn: 'any'
})
export class ChatService {

  private messages$ = new Subject<Message>();

  private connectionState$ = new Subject<boolean>();

  private socket$: WebSocketSubject<AgentModel> | undefined;

  constructor(private errorService: ErrorService) {
  }

  isChatResponse(obj: any): obj is ChatResponse {
    return obj && obj.type && obj.type === "chat-response";
  }

  public connect(url: string): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket({
        url: url
      });
    }
    this.socket$?.pipe(
      catchError(err => {
          this.errorService.throwError(err);
          this.close();
          throw err;
        }
      ),
      finalize(() => this.close())
    ).subscribe(
      msg => {
        if (this.isChatResponse(msg)) {
          let response = { text: msg.message, sender: 'server', chatId: msg.chatId } as Message
          this.messages$.next(response)
        }
      }
    );
    this.connectionState$.next(true);
  }

  public sendMessage(msg: Message): void {
    if (msg.chatId) {
      let request = { message: msg.text, chatId: msg.chatId, type: "chat-request" } as ChatRequest
      this.socket$?.next(request);
    } else {
      let request = { message: msg.text, type: "new-chat-request" } as NewChatRequest
      this.socket$?.next(request);
    }
  }

  public close(): void {
    this.socket$?.complete();
    this.socket$ = undefined;
    this.connectionState$.next(false);
  }

  public get serverAnswer$(): Observable<Message> {
    return this.messages$.pipe(
      filter(msg => msg.sender === 'server')
    );
  }

  public get connected$(): Observable<boolean> {
    return this.connectionState$;
  }
}
