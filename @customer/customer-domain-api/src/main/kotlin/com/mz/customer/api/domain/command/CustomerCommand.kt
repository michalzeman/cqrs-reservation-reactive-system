package com.mz.customer.api.domain.command

import com.mz.customer.api.domain.event.CustomerRegistered
import com.mz.customer.api.domain.event.CustomerReservationConfirmed
import com.mz.customer.api.domain.event.CustomerReservationDeclined
import com.mz.customer.api.domain.event.CustomerReservationRequested
import com.mz.ddd.common.api.domain.*
import kotlinx.datetime.Instant

sealed class CustomerCommand : DomainCommand

data class RegisterCustomer(
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun RegisterCustomer.toEvent(customerId: Id): CustomerRegistered {
    return CustomerRegistered(
        customerId = customerId,
        correlationId = this.correlationId,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email
    )
}

data class RequestNewCustomerReservation(
    val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun RequestNewCustomerReservation.toEvent(): CustomerReservationRequested {
    return CustomerReservationRequested(
        customerId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}

data class UpdateCustomerReservationAsConfirmed(
    val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun UpdateCustomerReservationAsConfirmed.toEvent(): CustomerReservationConfirmed {
    return CustomerReservationConfirmed(
        customerId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}

data class UpdateCustomerReservationAsDeclined(
    val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun UpdateCustomerReservationAsDeclined.toEvent(): CustomerReservationDeclined {
    return CustomerReservationDeclined(
        customerId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}