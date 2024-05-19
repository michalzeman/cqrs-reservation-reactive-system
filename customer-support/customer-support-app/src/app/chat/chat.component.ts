import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {FormsModule} from "@angular/forms";
import {ChatService, Message} from './chat.service';
import {map, switchMap} from "rxjs/operators";
import {ErrorComponent} from "../error/error.component";
import {CustomerDocument, CustomerService} from "../customer/customer.service";
import {MarkdownComponent} from "ngx-markdown";

const idPathParam = 'id';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    NgClass,
    NgForOf,
    FormsModule,
    NgIf,
    ErrorComponent,
    MarkdownComponent
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {

  messageToSend: string = '';
  isConnected: boolean = false;
  chatId?: string;
  customer?: CustomerDocument;

  private wsUrl = environment.wsApiUrl + '/ai-agent/chat-stream'

  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    private chatService: ChatService,
    private customerService: CustomerService
  ) {
    chatService.serverAnswer$.pipe(
      map(message => this.handleChatMessage(message))
    ).subscribe();

    chatService.connected$.subscribe(state => this.isConnected = state);
  }

  private handleChatMessage(message: Message) {
    let activeMsg = this.messages[this.messages.length - 1];
    activeMsg.text = activeMsg.text.concat(message.text);
    this.chatId = message.chatId;
    return activeMsg;
  }

  ngOnInit(): void {
    this.route.params.pipe(
     map(param => param[idPathParam]),
     switchMap(id => this.customerService.getById(id))
    ).subscribe(doc => {
      this.customer = doc;
      this.sendMessage('Hi');
    })
  }

  sendMessage(message?: string) {
    this.chatService.connect(this.wsUrl);
    this.isConnected = true;
    let userMessage: Message;
    if (message) {
      userMessage = {text: message, sender: 'user', chatId: this.chatId, customerData: this.customer}
    } else {
      userMessage = {text: this.messageToSend, sender: 'user', chatId: this.chatId, customerData: this.customer};
      this.messages.push(userMessage);
    }
    this.messages.push(({text: '', sender: 'server'}));
    this.chatService.sendMessage(userMessage);
    this.messageToSend = '';
  }

  get buttonDisabled(): boolean {
    return this.isConnected
  }

  closeWebSocket() {
    this.chatService.close();
  }

  submitForm(event: any) {
    let keyboardEvent = event as KeyboardEvent;
    if (!keyboardEvent.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
}
