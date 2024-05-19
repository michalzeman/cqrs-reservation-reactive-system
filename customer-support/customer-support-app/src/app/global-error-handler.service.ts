import {ErrorHandler, Injectable} from '@angular/core';
import {ErrorService} from './error/error.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  constructor(private errorService: ErrorService) {
  }

  handleError(error: any): void {
    this.errorService.throwError(error.message ? error.message : error.toString());
  }
}
