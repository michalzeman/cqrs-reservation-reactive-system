import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {FormsModule} from "@angular/forms";
import {ChatService, Message} from './chat.service';
import {map} from "rxjs/operators";
import {ErrorComponent} from "../error/error.component";

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    NgClass,
    NgForOf,
    FormsModule,
    NgIf,
    ErrorComponent
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {

  messageToSend: string = '';
  isConnected: boolean = false;
  chatId?: string;
  customerId?: string;

  private wsUrl = environment.wsApiUrl + '/ai-agent/chat-stream'

  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private chatService: ChatService
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
    this.customerId = this.route.snapshot.paramMap.get('id') ?? undefined;
    console.log(`customer id:${this.customerId}`);
  }

  sendMessage() {
    this.chatService.connect(this.wsUrl);
    this.isConnected = true;
    let userMessage: Message = {text: this.messageToSend, sender: 'user', chatId: this.chatId};
    this.messages.push(userMessage);
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
