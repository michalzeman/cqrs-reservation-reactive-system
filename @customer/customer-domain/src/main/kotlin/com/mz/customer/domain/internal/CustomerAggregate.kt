package com.mz.customer.domain.internal

import com.mz.customer.api.domain.command.RegisterCustomer
import com.mz.customer.api.domain.command.RequestNewCustomerReservation
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.customer.api.domain.event.CustomerRegistered
import com.mz.customer.api.domain.event.CustomerReservationRequested
import com.mz.ddd.common.api.domain.*

sealed class Customer : Aggregate() {
    abstract val version: Version
}

fun Id.getAggregate(): Customer {
    return EmptyCustomerAggregate(this)
}

internal data class EmptyCustomerAggregate(override val aggregateId: Id, override val version: Version = Version(0)) :
    Customer() {
    fun verifyRegisterCustomer(cmd: RegisterCustomer): List<CustomerEvent> {
        return listOf(
            CustomerRegistered(
                customerId = aggregateId,
                correlationId = cmd.correlationId,
                firstName = cmd.firstName,
                lastName = cmd.lastName,
                email = cmd.email
            )
        )
    }

    fun apply(event: CustomerRegistered): CustomerAggregate {
        return CustomerAggregate(
            aggregateId = this.aggregateId,
            version = this.version,
            lastName = event.lastName,
            firstName = event.firstName,
            email = event.email
        )
    }
}

internal data class CustomerAggregate(
    override val aggregateId: Id,
    override val version: Version = Version(),
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    val reservations: List<Reservation> = emptyList()
) : Customer() {
    fun verifyRequestNewCustomerReservation(cmd: RequestNewCustomerReservation): List<CustomerEvent> {
        return if (reservations.any { item -> item.id == cmd.reservationId }) {
            throw RuntimeException("Can't create a new reservation id=${cmd.reservationId}, reservation is already requested")
        } else {
            return listOf(
                CustomerReservationRequested(
                    customerId = aggregateId,
                    correlationId = cmd.correlationId,
                    reservationId = cmd.reservationId
                )
            )
        }
    }

    fun apply(event: CustomerReservationRequested): CustomerAggregate {
        val reservation = Reservation(event.reservationId, ReservationStatus.REQUESTED)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }
}
