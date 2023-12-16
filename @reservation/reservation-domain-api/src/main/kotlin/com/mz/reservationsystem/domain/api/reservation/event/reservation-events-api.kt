package com.mz.reservationsystem.domain.api.reservation.event

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

sealed class ReservationEvent : DomainEvent {
    abstract val reservationId: Id
}

data class ReservationRequested(
    override val reservationId: Id,
    val customerId: Id,
    val requestId: Id,
    val reservationTime: Instant,
    val numberOfPeople: Int = 0,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : ReservationEvent()