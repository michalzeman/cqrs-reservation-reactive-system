import {Component, OnInit} from '@angular/core';
import {ErrorService} from "./error.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-error',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss'
})
export class ErrorComponent implements OnInit {


  errorMessage: string | undefined;

  constructor(private errorService: ErrorService) {
  }

  ngOnInit(): void {
    this.errorService.error$
      .subscribe(message => {
        this.errorMessage = message
      });
  }

  clearMessages(): void {
    this.errorMessage = undefined;
  }
}
