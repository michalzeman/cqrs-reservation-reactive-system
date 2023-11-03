package com.mz.customer.api.domain.command

import com.mz.customer.api.domain.event.CustomerRegistered
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
) : CustomerCommand() {

    fun mapToEvent(customerId: Id): CustomerRegistered {
        return CustomerRegistered(
            customerId = customerId,
            correlationId = this.correlationId,
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email
        )
    }
}

data class RequestNewCustomerReservation(
    val customerId: Id,
    val reservationId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid())
) : CustomerCommand()
