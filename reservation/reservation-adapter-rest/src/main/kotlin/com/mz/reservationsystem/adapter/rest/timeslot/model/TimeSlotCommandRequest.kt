package com.mz.reservationsystem.adapter.rest.timeslot.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.domain.api.timeslot.BookTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.NEW_TIME_SLOT_ID
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import com.mz.reservationsystem.domain.api.timeslot.UpdateTimeSlot
import kotlinx.datetime.toKotlinInstant
import java.time.Instant


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateTimeSlotRequest::class, name = "create-time-slot"),
    JsonSubTypes.Type(value = UpdateTimeSlotRequest::class, name = "update-time-slot"),
    JsonSubTypes.Type(value = BookTimeSlotRequest::class, name = "book-time-slot")
)
sealed interface TimeSlotCommandRequest {
    fun toCommand(): TimeSlotCommand
}

data class BookTimeSlotRequest(
    val aggregateId: String,
    val booked: Boolean,
    val reservationId: String,
    val correlationId: String = uuid(),
    val commandId: String = uuid()
) : TimeSlotCommandRequest {
    override fun toCommand(): TimeSlotCommand {
        return BookTimeSlot(
            correlationId = Id(correlationId),
            commandId = Id(commandId),
            aggregateId = Id(aggregateId),
            booked = booked,
            reservationId = Id(reservationId)
        )
    }
}

data class CreateTimeSlotRequest(
    val startTime: Instant,
    val endTime: Instant,
    val correlationId: String = uuid(),
    val commandId: String = uuid(),
    val booked: Boolean = false
) : TimeSlotCommandRequest {
    override fun toCommand(): TimeSlotCommand {
        return CreateTimeSlot(
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            correlationId = Id(correlationId),
            commandId = Id(commandId),
            aggregateId = NEW_TIME_SLOT_ID,
            booked = booked
        )
    }
}

data class UpdateTimeSlotRequest(
    val aggregateId: String,
    val startTime: Instant,
    val endTime: Instant,
    val valid: Boolean,
    val reservationId: String? = null,
    val correlationId: String = uuid(),
    val commandId: String = uuid(),
    val booked: Boolean = false
) : TimeSlotCommandRequest {
    override fun toCommand(): TimeSlotCommand {
        return UpdateTimeSlot(
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            correlationId = Id(correlationId),
            commandId = Id(commandId),
            aggregateId = Id(aggregateId),
            booked = booked,
            valid = valid,
            reservationId = reservationId?.let { Id(it) }
        )
    }
}

