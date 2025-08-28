package com.mz.customer.application.internal

import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerEvent
import com.mz.customer.domain.api.CustomerRegistered
import com.mz.customer.domain.api.CustomerReservationConfirmed
import com.mz.customer.domain.api.CustomerReservationDeclined
import com.mz.customer.domain.api.CustomerReservationRequested
import com.mz.customer.domain.api.NEW_CUSTOMER_ID
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.domain.api.RequestNewCustomerReservation
import com.mz.customer.domain.api.Reservation
import com.mz.customer.domain.api.ReservationStatus
import com.mz.customer.domain.api.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.api.UpdateCustomerReservationAsDeclined
import com.mz.customer.domain.api.toEvent
import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.Version
import com.mz.ddd.common.api.domain.newId
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
) : Customer() {
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
    internal fun verifyRequestNewCustomerReservation(cmd: RequestNewCustomerReservation): List<CustomerEvent> {
        return if (reservations.existsReservation(cmd.requestId)) {
            error("Can't create a new reservation id=${cmd.requestId}, reservation is already requested")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    internal fun verifyUpdateCustomerReservationAsConfirmed(cmd: UpdateCustomerReservationAsConfirmed): List<CustomerEvent> {
        return if (!reservations.existsReservation(cmd.requestId)) {
            error("Can't confirm the reservation id=${cmd.reservationId}, reservation doesn't exist")
        } else {
            listOf(
                cmd.toEvent()
            )
        }
    }

    internal fun verifyUpdateCustomerReservationAsDeclined(cmd: UpdateCustomerReservationAsDeclined): List<CustomerEvent> {
        return if (reservations.existsReservation(cmd.reservationId) || reservations.existsReservation(cmd.requestId)) {
            listOf(
                cmd.toEvent()
            )
        } else {
            error("Can't decline the reservation id=${cmd.reservationId}, reservation doesn't exist")
        }
    }

    internal fun apply(event: CustomerReservationRequested): ExistingCustomer {
        val reservation = Reservation(event.requestId, status = ReservationStatus.REQUESTED, reservationPeriod = event.reservationPeriod)
        return this.copy(reservations = reservations.plus(reservation), version = version.increment())
    }

    internal fun apply(event: CustomerReservationConfirmed): ExistingCustomer {
        return this.copy(reservations = reservations.apply(event), version = version.increment())
    }

    internal fun apply(event: CustomerReservationDeclined): ExistingCustomer {
        return this.copy(reservations = reservations.apply(event), version = version.increment())
    }
}
