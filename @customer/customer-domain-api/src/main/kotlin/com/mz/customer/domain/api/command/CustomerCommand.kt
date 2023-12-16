package com.mz.customer.domain.api.command

import com.mz.customer.domain.api.event.CustomerRegistered
import com.mz.customer.domain.api.event.CustomerReservationConfirmed
import com.mz.customer.domain.api.event.CustomerReservationDeclined
import com.mz.customer.domain.api.event.CustomerReservationRequested
import com.mz.ddd.common.api.domain.*
import kotlinx.datetime.Instant

val NEW_CUSTOMER_ID = Id("new-customer")

sealed class CustomerCommand : DomainCommand {
    abstract val customerId: Id
}

data class RegisterCustomer(
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid()),
    override val customerId: Id = NEW_CUSTOMER_ID
) : CustomerCommand()

fun RegisterCustomer.toEvent(customerId: Id): CustomerRegistered {
    return CustomerRegistered(
        aggregateId = customerId,
        correlationId = this.correlationId,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email
    )
}

data class RequestNewCustomerReservation(
    override val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun RequestNewCustomerReservation.toEvent(): CustomerReservationRequested {
    return CustomerReservationRequested(
        aggregateId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}

data class UpdateCustomerReservationAsConfirmed(
    override val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun UpdateCustomerReservationAsConfirmed.toEvent(): CustomerReservationConfirmed {
    return CustomerReservationConfirmed(
        aggregateId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}

data class UpdateCustomerReservationAsDeclined(
    override val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun UpdateCustomerReservationAsDeclined.toEvent(): CustomerReservationDeclined {
    return CustomerReservationDeclined(
        aggregateId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}