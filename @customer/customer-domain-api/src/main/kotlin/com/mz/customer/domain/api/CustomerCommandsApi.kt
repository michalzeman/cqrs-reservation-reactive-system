package com.mz.customer.domain.api

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
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
    val reservationPeriod: ReservationPeriod,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun RequestNewCustomerReservation.toEvent(): CustomerReservationRequested {
    return CustomerReservationRequested(
        aggregateId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId,
        reservationPeriod = this.reservationPeriod
    )
}

data class UpdateCustomerReservationAsConfirmed(
    override val customerId: Id,
    val reservationId: Id,
    val requestId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun UpdateCustomerReservationAsConfirmed.toEvent(): CustomerReservationConfirmed {
    return CustomerReservationConfirmed(
        aggregateId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId,
        requestId = this.requestId
    )
}

data class UpdateCustomerReservationAsDeclined(
    override val customerId: Id,
    val reservationId: Id,
    val requestId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()

fun UpdateCustomerReservationAsDeclined.toEvent(): CustomerReservationDeclined {
    return CustomerReservationDeclined(
        aggregateId = this.customerId,
        reservationId = this.reservationId,
        correlationId = this.correlationId,
        requestId = this.reservationId
    )
}