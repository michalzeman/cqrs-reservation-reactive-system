import {Routes} from '@angular/router';
import {ChatComponent} from "./chat/chat.component";
import {CustomerComponent} from "./customer/customer.component";

export const routes: Routes = [
  { path: '', component: ChatComponent },
  { path: 'chat', component: ChatComponent },
  { path: 'chat/customer/:id', component: ChatComponent },
  { path: 'registration', component: CustomerComponent }
];
