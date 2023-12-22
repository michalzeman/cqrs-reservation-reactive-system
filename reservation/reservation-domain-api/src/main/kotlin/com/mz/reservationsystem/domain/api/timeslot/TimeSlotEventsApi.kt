package com.mz.reservationsystem.domain.api.timeslot

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TimeSlotEvent : DomainEvent {
    abstract val aggregateId: Id
}

@Serializable
@SerialName("time-slot-booked")
data class TimeSlotBooked(
    override val aggregateId: Id,
    val booked: Boolean,
    val reservationId: Id,
    override val correlationId: Id = newId(),
    override val eventId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : TimeSlotEvent()

@Serializable
@SerialName("time-slot-created")
data class TimeSlotCreated(
    override val aggregateId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : TimeSlotEvent()

@Serializable
@SerialName("time-slot-updated")
data class TimeSlotUpdated(
    override val aggregateId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    val reservationId: Id? = null,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : TimeSlotEvent()