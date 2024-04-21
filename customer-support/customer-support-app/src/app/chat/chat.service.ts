import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {catchError, filter, finalize, map} from 'rxjs/operators';
import {Observable, Subject} from 'rxjs';
import {ErrorService} from "../error/error.service";

export type Sender = 'user' | 'server';

export type Message = {
  text: string;
  sender: Sender;
};

@Injectable({
  providedIn: 'any'
})
export class ChatService {

  private messages$ = new Subject<Message>();

  private connectionState$ = new Subject<boolean>();

  private socket$: WebSocketSubject<string> | undefined;

  constructor(private errorService: ErrorService) {
  }

  public connect(url: string): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket({
        url: url,
        deserializer: msg => msg.data
      });
    }
    this.socket$?.pipe(
      map(word => ({text: word, sender: 'server' as Sender})),
      catchError(err => {
          this.errorService.throwError(err);
          this.close();
          throw err;
        }
      ),
      finalize(() => this.close())
    ).subscribe(
      msg => this.messages$.next(msg)
    );
    this.connectionState$.next(true);
  }

  public sendMessage(msg: Message): void {
    this.socket$?.next(msg.text);
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
