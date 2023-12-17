package com.mz.reservationsystem.domain.api.timeslot

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TimeSlotEvent : DomainEvent {
    abstract val timeSlotId: Id
}

@Serializable
@SerialName("time-slot-created")
data class TimeSlotCreated(
    override val timeSlotId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    override val correlationId: Id,
    override val createdAt: Instant,
    override val eventId: Id
) : TimeSlotEvent()

data class TimeSlotUpdated(
    override val timeSlotId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    override val correlationId: Id,
    override val createdAt: Instant,
    override val eventId: Id
) : TimeSlotEvent()