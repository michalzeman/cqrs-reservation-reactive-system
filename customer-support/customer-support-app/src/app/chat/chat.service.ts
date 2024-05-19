import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {catchError, filter, finalize} from 'rxjs/operators';
import {Observable, Subject} from 'rxjs';
import {ErrorService} from "../error/error.service";
import {CustomerDocument} from "../customer/customer.service";

export type Sender = 'user' | 'server' | 'hidden';

export type Message = {
  chatId?: string;
  customerData?: CustomerDocument;
  text: string;
  sender: Sender;
};

export type AgentModel = AgentRequest | AgentResponse;

export type AgentRequest = NewChatRequest | ChatRequest | NewChatCustomerRequest | ChatCustomerRequest;

export type AgentResponse = ChatResponse

export type NewChatRequest = {
  type: "new-chat-request";
  message: string;
}

export type NewChatCustomerRequest = {
  message: string;
  customerId: string;
  customerData: string;
};

export type ChatCustomerRequest = {
  chatId: string;
  message: string;
  customerId: string;
  customerData: string;
};

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

const url = "/api/ai-agent/stream-chats";

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
    let request: AgentRequest
    if (msg.chatId) {
      if (msg.customerData) {
        request = {message: msg.text, chatId: msg.chatId, customerId: msg.customerData.aggregateId, customerData: JSON.stringify(msg.customerData), type: "chat-customer-request"} as ChatCustomerRequest
      } else {
        request = {message: msg.text, chatId: msg.chatId, type: "chat-request"} as ChatRequest
      }
    } else {
      if (msg.customerData) {
        request = {message: msg.text, customerId: msg.customerData.aggregateId, customerData: JSON.stringify(msg.customerData), type: "new-chat-customer-request"} as NewChatCustomerRequest
      } else {
        request = {message: msg.text, type: "new-chat-request"} as NewChatRequest
      }
    }
    this.socket$?.next(request);
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
