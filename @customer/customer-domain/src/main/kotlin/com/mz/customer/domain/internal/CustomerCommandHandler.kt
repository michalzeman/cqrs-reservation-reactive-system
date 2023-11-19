package com.mz.customer.domain.internal

import com.mz.customer.api.domain.command.CustomerCommand
import com.mz.customer.api.domain.command.RegisterCustomer
import com.mz.customer.api.domain.command.RequestNewCustomerReservation
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler


class CustomerCommandHandler : AggregateCommandHandler<Customer, CustomerCommand, CustomerEvent> {

    override fun execute(aggregate: Customer, command: CustomerCommand): Result<List<CustomerEvent>> {
        return when (aggregate) {
            is EmptyCustomerAggregate -> newCustomer(aggregate, command)
            is CustomerAggregate -> existingCustomer(aggregate, command)
        }
    }

    private fun newCustomer(aggregate: EmptyCustomerAggregate, cmd: CustomerCommand): Result<List<CustomerEvent>> {
        return when (cmd) {
            is RegisterCustomer -> Result.runCatching { aggregate.verifyRegisterCustomer(cmd) }
            else -> Result.failure(RuntimeException(""))
        }
    }

    private fun existingCustomer(aggregate: CustomerAggregate, cmd: CustomerCommand): Result<List<CustomerEvent>> {
        return when (cmd) {
            is RegisterCustomer -> Result.failure(RuntimeException(""))
            is RequestNewCustomerReservation -> Result.runCatching { aggregate.verifyRequestNewCustomerReservation(cmd) }
        }
    }
}