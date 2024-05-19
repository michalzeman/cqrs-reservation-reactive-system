import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ErrorComponent} from "../error/error.component";
import {tap} from 'rxjs/operators';
import {CustomerService, RegisterCustomerRequest} from "./customer.service";

@Component({
  selector: 'app-new-customer-component',
  standalone: true,
  imports: [
    NgClass,
    NgForOf,
    FormsModule,
    NgIf,
    ErrorComponent,
    ReactiveFormsModule
  ],
  templateUrl: './customer-component.html',
  styleUrl: './customer-component.scss'
})
export class CustomerComponent implements OnInit {

  firstName: FormControl;
  lastName: FormControl;
  email: FormControl;

  customerForm: FormGroup;

  constructor(
    private customerService: CustomerService,
    private router: Router
  ) {
    this.firstName = new FormControl(null, Validators.required);
    this.lastName = new FormControl(null, Validators.required);
    this.email = new FormControl(null, [Validators.required, Validators.email]);
    this.customerForm = new FormGroup({
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email
    });
  }

  ngOnInit() {
  }

  onSubmit() {
    let customer = new RegisterCustomerRequest(
      this.lastName.value,
      this.firstName.value,
      this.email.value
    );
    this.customerService.createCustomer(customer)
      .pipe(
        tap(customer => this.router.navigate([`chat/customer/${customer.aggregateId}`]))
      )
      .subscribe();
  }
}
