package com.mz.customer.domain.internal

import com.mz.customer.api.domain.command.*
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler


class CustomerCommandHandler : AggregateCommandHandler<Customer, CustomerCommand, CustomerEvent> {

    override fun execute(aggregate: Customer, command: CustomerCommand): Result<List<CustomerEvent>> {
        return when (aggregate) {
            is EmptyCustomer -> newCustomer(aggregate, command)
            is ExistingCustomer -> existingCustomer(aggregate, command)
        }
    }

    private fun newCustomer(aggregate: EmptyCustomer, cmd: CustomerCommand): Result<List<CustomerEvent>> {
        return Result.runCatching {
            when (cmd) {
                is RegisterCustomer -> aggregate.verifyRegisterCustomer(cmd)
                else -> throw RuntimeException("Ca not apply command: ${cmd::class} on non existing aggregate")
            }
        }
    }

    private fun existingCustomer(aggregate: ExistingCustomer, cmd: CustomerCommand): Result<List<CustomerEvent>> {
        return Result.runCatching {
            when (cmd) {
                is RegisterCustomer -> throw RuntimeException("Can not apply command: ${cmd::class} on existing aggregate")
                is RequestNewCustomerReservation -> aggregate.verifyRequestNewCustomerReservation(cmd)
                is UpdateCustomerReservationAsConfirmed -> aggregate.verifyUpdateCustomerReservationAsConfirmed(cmd)
                is UpdateCustomerReservationAsDeclined -> aggregate.verifyUpdateCustomerReservationAsDeclined(cmd)
            }
        }
    }
}