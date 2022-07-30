package com.mz.customer.domain.internal

import com.mz.customer.api.domain.command.CustomerCommand
import com.mz.customer.api.domain.command.RegisterCustomer
import com.mz.customer.api.domain.command.RequestNewCustomerReservation
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.reservation.common.api.domain.command.AggregateCommandHandler
import com.mz.reservation.common.api.util.Failure
import com.mz.reservation.common.api.util.Try


internal class CustomerCommandHandler : AggregateCommandHandler<Customer, CustomerCommand, CustomerEvent> {

    override fun execute(aggregate: Customer, command: CustomerCommand): Try<List<CustomerEvent>> {
        return when (aggregate) {
            is EmptyCustomerAggregate -> newCustomer(aggregate, command)
            is CustomerAggregate -> existingCustomer(aggregate, command)
        }
    }

    private fun newCustomer(aggregate: EmptyCustomerAggregate, cmd: CustomerCommand): Try<List<CustomerEvent>> {
        return when (cmd) {
            is RegisterCustomer -> Try { aggregate.verifyRegisterCustomer(cmd) }
            else -> Failure(RuntimeException(""))
        }
    }

    private fun existingCustomer(aggregate: CustomerAggregate, cmd: CustomerCommand): Try<List<CustomerEvent>> {
        return when (cmd) {
            is RegisterCustomer -> Failure(RuntimeException(""))
            is RequestNewCustomerReservation -> Try { aggregate.verifyRequestNewCustomerReservation(cmd) }
        }
    }
}