import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import {catchError} from "rxjs/operators";
import {ErrorService} from "../error/error.service";

const url: string = "/api/customers"

export abstract class CustomerCommandRequest {
  protected type: string;

  protected constructor(type: string) {
    this.type = type;
  }
}

export class RegisterCustomerRequest extends CustomerCommandRequest {
  lastName: string;
  firstName: string;
  email: string;

  constructor(
    lastName: string,
    firstName: string,
    email: string
  ) {
    super('register-customer');
    this.lastName = lastName;
    this.firstName = firstName;
    this.email = email;
  }
}

export class CustomerDocument {
  aggregateId: string;
  lastName: string;
  firstName: string;
  email: string;
  version: number;
  docId: string;
  correlationId: string;

  constructor() {
    this.aggregateId = '';
    this.lastName = '';
    this.firstName = '';
    this.email = '';
    this.version = 0;
    this.docId = '';
    this.correlationId = '';
  }
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(private httpClient: HttpClient, private errorService: ErrorService) {
  }

  createCustomer(customer: RegisterCustomerRequest): Observable<CustomerDocument> {
    return this.httpClient.post<CustomerDocument>(url, customer)
      .pipe(
        catchError(err => {
            this.errorService.throwError(err);
            throw err;
          }
        )
      );
  }

  getById(id: string): Observable<CustomerDocument> {
    return this.httpClient.get<CustomerDocument>(`${url}/${id}`)
      .pipe(
        catchError(err => {
            this.errorService.throwError(err);
            throw err;
          }
        )
      );
  }
}
