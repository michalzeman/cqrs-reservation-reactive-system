package com.mz.customer.domain.internal

import com.mz.customer.api.domain.CustomerDocument
import com.mz.customer.api.domain.Reservation
import com.mz.customer.api.domain.ReservationStatus
import com.mz.customer.api.domain.command.*
import com.mz.customer.api.domain.event.*
import com.mz.customer.api.domain.existsReservation
import com.mz.ddd.common.api.domain.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val CUSTOMER_DOMAIN_TAG = DomainTag("customer")

@Serializable
sealed class Customer : Aggregate() {
    abstract val version: Version
}

fun Customer.toDocument(events: Set<CustomerEvent> = emptySet()): CustomerDocument {
    return when (this) {
        is EmptyCustomer -> error("Customer is not registered yet")
        is ExistingCustomer -> CustomerDocument(
            lastName = lastName,
            firstName = firstName,
            email = email,
            version = version,
            aggregateId = aggregateId,
            reservations = reservations,
            events = events
        )
    }
}

fun Id.getAggregate(): Customer {
    return when (this) {
        NEW_CUSTOMER_ID -> EmptyCustomer(newId())
        else -> EmptyCustomer(this)
    }
}

@Serializable
@SerialName("empty-customer")
internal data class EmptyCustomer(
    override val aggregateId: Id = NEW_CUSTOMER_ID,
    override val version: Version = Version(0)
) :
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

@Serializable
@SerialName("existing-customer")
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
            error("Can't create a new reservation id=${cmd.reservationId}, reservation is already requested")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    fun verifyUpdateCustomerReservationAsConfirmed(cmd: UpdateCustomerReservationAsConfirmed): List<CustomerEvent> {
        return if (!reservations.existsReservation(cmd.reservationId)) {
            error("Can't confirm the reservation id=${cmd.reservationId}, reservation doesn't exist")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    fun verifyUpdateCustomerReservationAsDeclined(cmd: UpdateCustomerReservationAsDeclined): List<CustomerEvent> {
        return if (!reservations.existsReservation(cmd.reservationId)) {
            error("Can't decline the reservation id=${cmd.reservationId}, reservation doesn't exist")
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
        val reservation = Reservation(event.reservationId, ReservationStatus.CONFIRMED)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }

    fun apply(event: CustomerReservationDeclined): ExistingCustomer {
        val reservation = Reservation(event.reservationId, ReservationStatus.DECLINED)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }
}
