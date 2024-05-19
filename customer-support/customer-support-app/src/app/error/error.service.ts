import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  public error$: Subject<string> = new Subject();

  constructor() {
  }

  public throwError(message: any): void {
    this.error$.next(JSON.stringify(message));
  }
}
