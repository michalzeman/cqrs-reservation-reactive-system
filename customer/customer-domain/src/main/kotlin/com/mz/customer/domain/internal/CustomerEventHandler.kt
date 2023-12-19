package com.mz.customer.domain.internal

import com.mz.customer.domain.api.CustomerEvent
import com.mz.customer.domain.api.CustomerRegistered
import com.mz.customer.domain.api.CustomerReservationConfirmed
import com.mz.customer.domain.api.CustomerReservationDeclined
import com.mz.customer.domain.api.CustomerReservationRequested
import com.mz.ddd.common.api.domain.event.AggregateEventHandler

class CustomerEventHandler : AggregateEventHandler<Customer, CustomerEvent> {
    override fun apply(aggregate: Customer, event: CustomerEvent): Customer {
        return when (aggregate) {
            is EmptyCustomer -> newCustomer(aggregate, event)
            is ExistingCustomer -> existingCustomer(aggregate, event)
        }
    }

    private fun newCustomer(aggregate: EmptyCustomer, event: CustomerEvent): ExistingCustomer {
        return when (event) {
            is CustomerRegistered -> aggregate.apply(event)
            else -> throw RuntimeException("Wrong event type $event for the empty customer aggregate")
        }
    }

    private fun existingCustomer(aggregate: ExistingCustomer, event: CustomerEvent): ExistingCustomer {
        return when (event) {
            is CustomerRegistered -> throw RuntimeException("Wrong event type ${event::class} for the existing customer aggregate")
            is CustomerReservationRequested -> aggregate.apply(event)
            is CustomerReservationConfirmed -> aggregate.apply(event)
            is CustomerReservationDeclined -> aggregate.apply(event)
        }
    }
}