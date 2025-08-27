import {Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef} from '@angular/core';
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

  @ViewChild('chatMessages', { static: false }) private chatMessagesContainer!: ElementRef;

  messageToSend: string = '';
  isConnected: boolean = false;
  chatId?: string;
  customer?: CustomerDocument;

  private wsUrl = environment.wsApiUrl + '/ai-agent/chat-stream'

  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    private chatService: ChatService,
    private customerService: CustomerService,
    private cdr: ChangeDetectorRef
  ) {
    chatService.serverAnswer$.pipe(
      map(message => this.handleChatMessage(message))
    ).subscribe();

    chatService.connected$.subscribe(state => {
      const wasConnected = this.isConnected;
      this.isConnected = state;

      // If connection just ended (streaming finished), ensure we stay scrolled to bottom
      if (wasConnected && !state) {
        setTimeout(() => {
          this.scrollToBottom();
        }, 50);
      }
    });
  }

  private handleChatMessage(message: Message) {
    let activeMsg = this.messages[this.messages.length - 1];
    activeMsg.text = activeMsg.text.concat(message.text);
    this.chatId = message.chatId;

    // Force change detection and scroll after DOM update
    setTimeout(() => {
      this.scrollToBottom();
    }, 0);

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

    // Scroll to bottom after adding new messages
    setTimeout(() => {
      this.scrollToBottom();
    }, 0);
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

  private scrollToBottom(): void {
    try {
      if (this.chatMessagesContainer?.nativeElement) {
        const element = this.chatMessagesContainer.nativeElement;
        element.scrollTo({
          top: element.scrollHeight,
          behavior: 'smooth'
        });
      }
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }
}
