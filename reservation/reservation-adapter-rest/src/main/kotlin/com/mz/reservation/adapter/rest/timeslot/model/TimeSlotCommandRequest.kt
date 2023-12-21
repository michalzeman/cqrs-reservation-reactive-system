package com.mz.reservation.adapter.rest.timeslot.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.NEW_TIME_SLOT_ID
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import kotlinx.datetime.Instant

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateTimeSlotRequest::class, name = "create-time-slot"),
    JsonSubTypes.Type(value = UpdateTimeSlotRequest::class, name = "update-time-slot"),
)
sealed interface TimeSlotCommandRequest {
    fun toCommand(): TimeSlotCommand
}

data class CreateTimeSlotRequest(
    val startTime: Instant,
    val endTime: Instant,
    val correlationId: String = uuid(),
    val commandId: String = uuid(),
    val boolean: Boolean = false
) : TimeSlotCommandRequest {
    override fun toCommand(): TimeSlotCommand {
        return CreateTimeSlot(
            startTime = startTime,
            endTime = endTime,
            correlationId = Id(correlationId),
            commandId = Id(commandId),
            aggregateId = NEW_TIME_SLOT_ID,
            booked = boolean
        )
    }
}

data class UpdateTimeSlotRequest(
    val aggregateId: String,
    val startTime: Instant,
    val endTime: Instant,
    val correlationId: String = uuid(),
    val commandId: String = uuid(),
    val boolean: Boolean = false
) : TimeSlotCommandRequest {
    override fun toCommand(): TimeSlotCommand {
        return CreateTimeSlot(
            startTime = startTime,
            endTime = endTime,
            correlationId = Id(correlationId),
            commandId = Id(commandId),
            aggregateId = Id(aggregateId),
            booked = boolean
        )
    }
}

