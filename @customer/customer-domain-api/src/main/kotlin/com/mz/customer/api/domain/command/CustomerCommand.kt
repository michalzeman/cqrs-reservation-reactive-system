package com.mz.customer.api.domain.command

import com.mz.customer.api.domain.event.CustomerRegistered
import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import kotlinx.datetime.Instant

sealed class CustomerCommand : DomainCommand()

data class RegisterCustomer(
    val lastName: String,
    val firstName: String,
    val email: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val commandId: String = uuid()
) : CustomerCommand() {

    fun mapToEvent(customerId: String): CustomerRegistered {
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
    val customerId: String,
    val reservationId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val commandId: String = uuid()
) : CustomerCommand()
