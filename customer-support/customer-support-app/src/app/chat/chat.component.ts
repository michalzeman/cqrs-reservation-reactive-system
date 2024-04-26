import { Component, ElementRef, ViewChild} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
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
export class ChatComponent {

  newMessage: string = '';
  isConnected: boolean = false;

  private wsUrl = 'ws://localhost:8080/ai-agent/chat-stream'
  // private wsUrl = 'ws://localhost:4200/ai-agent/chat-stream'

  messages: Message[] = [];
  // messages: Message[] = Array.from({length: 20}, (_, i) => {
  //   return {
  //     text: `Random message ${Math.floor(Math.random() * 1000)}`,
  //     sender: i % 2 === 0 ? 'user' : 'server'
  //   };
  // });

  constructor(private chatService: ChatService) {
    chatService.serverAnswer$.pipe(
      map(text => {
        let activeMsg = this.messages[this.messages.length - 1];
        activeMsg.text = activeMsg.text.concat(text.text);
        return activeMsg;
      })
    ).subscribe();

    chatService.connected$.subscribe(state => this.isConnected = state);
  }

  sendMessage() {
    this.chatService.connect(this.wsUrl);
    this.isConnected = true;
    let userMessage: Message = {text: this.newMessage, sender: 'user'};
    this.messages.push(userMessage);
    this.messages.push(({text: '', sender: 'server'}));
    this.chatService.sendMessage(userMessage);
    this.newMessage = '';
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
