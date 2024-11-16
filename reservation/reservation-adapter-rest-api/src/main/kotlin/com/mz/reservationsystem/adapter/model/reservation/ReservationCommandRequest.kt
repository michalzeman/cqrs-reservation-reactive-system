package com.mz.reservationsystem.adapter.model.reservation

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.domain.api.reservation.AcceptReservation
import com.mz.reservationsystem.domain.api.reservation.DeclineReservation
import com.mz.reservationsystem.domain.api.reservation.NEW_RESERVATION_ID
import com.mz.reservationsystem.domain.api.reservation.RequestReservation
import com.mz.reservationsystem.domain.api.reservation.ReservationCommand
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RequestReservationRequest::class, name = "request-reservation"),
    JsonSubTypes.Type(value = AcceptReservationRequest::class, name = "accept-reservation"),
    JsonSubTypes.Type(value = DeclineReservationRequest::class, name = "decline-reservation")
)
sealed interface ReservationCommandRequest {
    fun toCommand(): ReservationCommand
}

data class RequestReservationRequest(
    val customerId: String,
    val requestId: String,
    val startTime: Instant,
    val endTime: Instant,
    val correlationId: String = uuid(),
    val commandId: String = uuid()
) : ReservationCommandRequest {
    override fun toCommand(): ReservationCommand {
        return RequestReservation(
            aggregateId = NEW_RESERVATION_ID,
            customerId = Id(customerId),
            requestId = Id(requestId),
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            correlationId = Id(correlationId),
            commandId = Id(commandId)
        )
    }
}

data class AcceptReservationRequest(
    val aggregateId: String,
    val timeSlotId: String,
    val correlationId: String = uuid(),
    val commandId: String = uuid()
) : ReservationCommandRequest {
    override fun toCommand(): ReservationCommand {
        return AcceptReservation(
            aggregateId = Id(aggregateId),
            timeSlotId = Id(timeSlotId),
            correlationId = Id(correlationId),
            commandId = Id(commandId)
        )
    }
}

data class DeclineReservationRequest(
    val aggregateId: String,
    val correlationId: String = uuid(),
    val commandId: String = uuid()
) : ReservationCommandRequest {
    override fun toCommand(): ReservationCommand {
        return DeclineReservation(
            aggregateId = Id(aggregateId),
            correlationId = Id(correlationId),
            commandId = Id(commandId)
        )
    }
}
