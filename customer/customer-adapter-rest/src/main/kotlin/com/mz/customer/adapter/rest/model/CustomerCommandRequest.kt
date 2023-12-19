package com.mz.customer.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.mz.customer.domain.api.CustomerCommand
import com.mz.customer.domain.api.NEW_CUSTOMER_ID
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.domain.api.RequestNewCustomerReservation
import com.mz.customer.domain.api.ReservationPeriod
import com.mz.customer.domain.api.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.api.UpdateCustomerReservationAsDeclined
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import kotlinx.datetime.Instant


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RegisterCustomerRequest::class, name = "register-customer"),
    JsonSubTypes.Type(value = NewCustomerReservationRequest::class, name = "new-customer-reservation"),
    JsonSubTypes.Type(
        value = UpdateCustomerReservationAsConfirmedRequest::class,
        name = "update-customer-reservation-as-confirmed"
    ),
    JsonSubTypes.Type(
        value = UpdateCustomerReservationAsDeclinedRequest::class,
        name = "update-customer-reservation-as-declined"
    )
)
sealed interface CustomerCommandRequest {
    fun toCommand(): CustomerCommand
}

data class RegisterCustomerRequest(
    val lastName: String,
    val firstName: String,
    val email: String,
    val correlationId: String = uuid(),
    val createdAt: Instant = instantNow(),
    val commandId: String = uuid(),
    val customerId: String = NEW_CUSTOMER_ID.value
) : CustomerCommandRequest {
    override fun toCommand(): CustomerCommand {
        return RegisterCustomer(
            lastName = LastName(lastName),
            firstName = FirstName(firstName),
            email = Email(email),
            correlationId = Id(correlationId),
            createdAt = createdAt,
            commandId = Id(commandId),
            customerId = Id(customerId)
        )
    }
}

data class NewCustomerReservationRequest(
    val customerId: String,
    val reservationId: String,
    val startTime: Instant,
    val endTime: Instant,
    val correlationId: String = uuid(),
    val createdAt: Instant = instantNow(),
    val commandId: String = uuid()
) : CustomerCommandRequest {
    override fun toCommand(): CustomerCommand {
        return RequestNewCustomerReservation(
            customerId = Id(customerId),
            reservationId = Id(reservationId),
            reservationPeriod = ReservationPeriod(startTime, endTime),
            correlationId = Id(correlationId),
            createdAt = createdAt,
            commandId = Id(commandId)
        )
    }
}

data class UpdateCustomerReservationAsConfirmedRequest(
    val customerId: String,
    val reservationId: String,
    val requestId: String,
    val correlationId: String = uuid(),
    val createdAt: Instant = instantNow(),
    val commandId: String = uuid()
) : CustomerCommandRequest {
    override fun toCommand(): CustomerCommand {
        return UpdateCustomerReservationAsConfirmed(
            customerId = Id(customerId),
            requestId = Id(requestId),
            reservationId = Id(reservationId),
            correlationId = Id(correlationId),
            createdAt = createdAt,
            commandId = Id(commandId)
        )
    }
}

data class UpdateCustomerReservationAsDeclinedRequest(
    val customerId: String,
    val reservationId: String,
    val requestId: String,
    val correlationId: String = uuid(),
    val createdAt: Instant = instantNow(),
    val commandId: String = uuid()
) : CustomerCommandRequest {
    override fun toCommand(): CustomerCommand {
        return UpdateCustomerReservationAsDeclined(
            customerId = Id(customerId),
            requestId = Id(requestId),
            reservationId = Id(reservationId),
            correlationId = Id(correlationId),
            createdAt = createdAt,
            commandId = Id(commandId)
        )
    }
}
