package com.mz.reservationsystem.domain.api.timeslot

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

val NEW_TIME_SLOT_ID: Id = Id("new-time-slot")

val TIME_SLOT_DOMAIN_TAG = DomainTag("time-slot")

sealed class TimeSlotCommand : DomainCommand {
    abstract val aggregateId: Id
}

data class BookTimeSlot(
    override val aggregateId: Id,
    val reservationId: Id,
    val booked: Boolean,
    override val correlationId: Id = newId(),
    override val commandId: Id = newId(),
    override val createdAt: Instant = instantNow(),
) : TimeSlotCommand()

fun BookTimeSlot.toEvent(): TimeSlotBooked {
    return TimeSlotBooked(
        aggregateId = this.aggregateId,
        booked = this.booked,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}

data class CreateTimeSlot(
    override val aggregateId: Id = NEW_TIME_SLOT_ID,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = newId()
) : TimeSlotCommand()

fun CreateTimeSlot.toEvent(aggregateId: Id): TimeSlotCreated {
    return TimeSlotCreated(
        aggregateId = aggregateId,
        startTime = this.startTime,
        endTime = this.endTime,
        booked = this.booked,
        correlationId = this.correlationId
    )
}

data class UpdateTimeSlot(
    override val aggregateId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = newId(),
    val reservationId: Id? = null,
) : TimeSlotCommand()

fun UpdateTimeSlot.toEvent(): TimeSlotUpdated {
    return TimeSlotUpdated(
        aggregateId = this.aggregateId,
        startTime = this.startTime,
        endTime = this.endTime,
        booked = this.booked,
        valid = this.valid,
        reservationId = this.reservationId,
        correlationId = this.correlationId
    )
}