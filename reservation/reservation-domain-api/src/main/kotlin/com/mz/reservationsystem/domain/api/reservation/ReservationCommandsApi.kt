package com.mz.reservationsystem.domain.api.reservation

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

val NEW_RESERVATION_ID: Id = Id("new-reservation")

val RESERVATION_DOMAIN_TAG = DomainTag("reservation")

sealed class ReservationCommand : DomainCommand {
    abstract val aggregateId: Id
}

data class RequestReservation(
    override val aggregateId: Id,
    val customerId: Id,
    val requestId: Id,
    val startTime: Instant,
    val endTime: Instant,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = newId()
) : ReservationCommand()

fun RequestReservation.toDomainEvent(aggregateId: Id): ReservationRequested {
    return ReservationRequested(
        aggregateId = aggregateId,
        customerId = this.customerId,
        requestId = this.requestId,
        startTime = this.startTime,
        endTime = this.endTime,
        correlationId = this.correlationId
    )
}

data class AcceptReservation(
    override val aggregateId: Id,
    val timeSlotId: Id,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = newId()
) : ReservationCommand()

fun AcceptReservation.toDomainEvent(): ReservationAccepted {
    return ReservationAccepted(
        aggregateId = this.aggregateId,
        timeSlotId = this.timeSlotId,
        correlationId = this.correlationId
    )
}

data class DeclineReservation(
    override val aggregateId: Id,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = newId()
) : ReservationCommand()

fun DeclineReservation.toDomainEvent(): ReservationDeclined {
    return ReservationDeclined(
        aggregateId = this.aggregateId,
        correlationId = this.correlationId
    )
}

