package com.mz.customer.domain.internal

import com.mz.customer.api.domain.command.*
import com.mz.customer.api.domain.event.*
import com.mz.ddd.common.api.domain.*

sealed class Customer : Aggregate() {
    abstract val version: Version
}

fun Id.getAggregate(): Customer {
    return EmptyCustomer(this)
}

internal data class EmptyCustomer(override val aggregateId: Id, override val version: Version = Version(0)) :
    Customer() {
    fun verifyRegisterCustomer(cmd: RegisterCustomer): List<CustomerEvent> {
        return listOf(
            cmd.toEvent(aggregateId)
        )
    }

    fun apply(event: CustomerRegistered): ExistingCustomer {
        return ExistingCustomer(
            aggregateId = this.aggregateId,
            version = this.version,
            lastName = event.lastName,
            firstName = event.firstName,
            email = event.email
        )
    }
}

internal data class ExistingCustomer(
    override val aggregateId: Id,
    override val version: Version = Version(),
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    val reservations: Set<Reservation> = emptySet()
) : Customer() {
    fun verifyRequestNewCustomerReservation(cmd: RequestNewCustomerReservation): List<CustomerEvent> {
        return if (reservations.existsReservation(cmd.reservationId)) {
            throw RuntimeException("Can't create a new reservation id=${cmd.reservationId}, reservation is already requested")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    fun verifyUpdateCustomerReservationAsConfirmed(cmd: UpdateCustomerReservationAsConfirmed): List<CustomerEvent> {
        return if (reservations.existsReservation(cmd.reservationId)) {
            throw RuntimeException("Can't create a new reservation id=${cmd.reservationId}, reservation is already requested")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    fun verifyUpdateCustomerReservationAsDeclined(cmd: UpdateCustomerReservationAsDeclined): List<CustomerEvent> {
        return if (reservations.existsReservation(cmd.reservationId)) {
            throw RuntimeException("Can't create a new reservation id=${cmd.reservationId}, reservation is already requested")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    fun apply(event: CustomerReservationRequested): ExistingCustomer {
        val reservation = Reservation(event.reservationId, ReservationStatus.REQUESTED)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }

    fun apply(event: CustomerReservationConfirmed): ExistingCustomer {
        val reservation = Reservation(event.reservationId, ReservationStatus.CREATED)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }

    fun apply(event: CustomerReservationDeclined): ExistingCustomer {
        val reservation = Reservation(event.reservationId, ReservationStatus.DECLINED)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }
}
