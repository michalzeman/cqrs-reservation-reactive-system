package com.mz.customer.domain.internal

import com.mz.customer.api.domain.event.*
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.event.AggregateEventHandler

class CustomerEventHandler : AggregateEventHandler<Customer, CustomerEvent> {
    override fun apply(aggregate: Customer, event: CustomerEvent): Customer {
        return when (aggregate) {
            is EmptyCustomerAggregate -> newCustomer(aggregate, event)
            is CustomerAggregate -> existingCustomer(aggregate, event)
        }
    }

    private fun newCustomer(aggregate: EmptyCustomerAggregate, event: DomainEvent): CustomerAggregate {
        return when (event) {
            is CustomerRegistered -> aggregate.apply(event)
            else -> throw RuntimeException("Wrong event type $event for the empty customer aggregate")
        }
    }

    private fun existingCustomer(aggregate: CustomerAggregate, event: DomainEvent): CustomerAggregate {
        return when (event) {
            is CustomerRegistered -> throw RuntimeException("Wrong event type $event for the existing customer aggregate")
            is CustomerReservationRequested -> aggregate.apply(event)
            is CustomerReservationConfirmed -> TODO()
            is CustomerReservationDeclined -> TODO()
            else -> throw RuntimeException("Wrong event type $event for the empty customer aggregate")
        }
    }
}