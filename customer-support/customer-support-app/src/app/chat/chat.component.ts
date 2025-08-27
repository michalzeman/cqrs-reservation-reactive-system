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
  private rawMessageBuffer: string = ''; // Buffer to store the complete raw message

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

    // Append new text to the raw buffer
    this.rawMessageBuffer += message.text;

    // Process the buffer to filter out <think> content and update the displayed message
    activeMsg.text = this.filterThinkContent(this.rawMessageBuffer);
    this.chatId = message.chatId;

    // Force change detection and scroll after DOM update
    setTimeout(() => {
      this.scrollToBottom();
    }, 0);

    return activeMsg;
  }

  private filterThinkContent(text: string): string {
    let result = '';
    let currentPos = 0;
    let insideThink = false;

    while (currentPos < text.length) {
      const thinkStartIndex = text.indexOf('<think>', currentPos);
      const thinkEndIndex = text.indexOf('</think>', currentPos);

      if (!insideThink) {
        // We're outside a think block
        if (thinkStartIndex === -1) {
          // No more think blocks, add the rest of the text
          result += text.substring(currentPos);
          break;
        } else {
          // Found a think start, add text before it
          result += text.substring(currentPos, thinkStartIndex);
          insideThink = true;
          currentPos = thinkStartIndex + '<think>'.length;
        }
      } else {
        // We're inside a think block
        if (thinkEndIndex === -1) {
          // Think block not closed yet, skip the rest
          break;
        } else {
          // Found think end, skip to after it
          insideThink = false;
          currentPos = thinkEndIndex + '</think>'.length;
        }
      }
    }

    return result;
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

    // Reset the raw message buffer for new messages
    this.rawMessageBuffer = '';

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
