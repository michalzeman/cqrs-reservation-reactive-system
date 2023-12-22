package com.mz.reservationsystem.domain.api.reservation

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ReservationEvent : DomainEvent {
    abstract val aggregateId: Id
}

@Serializable
@SerialName("reservation-requested")
data class ReservationRequested(
    override val aggregateId: Id,
    val customerId: Id,
    val requestId: Id,
    val startTime: Instant,
    val endTime: Instant,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : ReservationEvent()

@Serializable
@SerialName("reservation-declined")
data class ReservationDeclined(
    override val aggregateId: Id,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : ReservationEvent()

@Serializable
@SerialName("reservation-accepted")
data class ReservationAccepted(
    override val aggregateId: Id,
    val timeSlotId: Id,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : ReservationEvent()
