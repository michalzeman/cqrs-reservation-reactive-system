import {Component, ErrorHandler} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import { provideMarkdown } from 'ngx-markdown';
import {ChatComponent} from "./chat/chat.component";
import {ErrorComponent} from "./error/error.component";
import {GlobalErrorHandler} from "./global-error-handler.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ChatComponent, ErrorComponent],
  providers: [{provide: ErrorHandler, useClass: GlobalErrorHandler}, provideMarkdown()],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'customer-support-app';
}
