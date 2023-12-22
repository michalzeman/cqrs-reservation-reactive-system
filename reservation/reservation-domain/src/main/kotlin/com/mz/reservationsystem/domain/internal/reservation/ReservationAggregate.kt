package com.mz.reservationsystem.domain.internal.reservation

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.reservationsystem.domain.api.reservation.AcceptReservation
import com.mz.reservationsystem.domain.api.reservation.DeclineReservation
import com.mz.reservationsystem.domain.api.reservation.RequestReservation
import com.mz.reservationsystem.domain.api.reservation.ReservationAccepted
import com.mz.reservationsystem.domain.api.reservation.ReservationCommand
import com.mz.reservationsystem.domain.api.reservation.ReservationDeclined
import com.mz.reservationsystem.domain.api.reservation.ReservationEvent
import com.mz.reservationsystem.domain.api.reservation.ReservationRequested
import com.mz.reservationsystem.domain.api.reservation.toDomainEvent
import com.mz.reservationsystem.domain.internal.reservation.ReservationState.ACCEPTED
import com.mz.reservationsystem.domain.internal.reservation.ReservationState.DECLINED
import com.mz.reservationsystem.domain.internal.reservation.ReservationState.REQUESTED
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ReservationState {
    NONE,
    REQUESTED,
    DECLINED,
    ACCEPTED
}

@Serializable
sealed class ReservationAggregate : Aggregate() {
    abstract fun verifyCommand(cmd: ReservationCommand): List<ReservationEvent>

    abstract fun applyEvent(event: ReservationEvent): ReservationAggregate
}

@Serializable
@SerialName("none-reservation-aggregate")
internal data class NoneReservationAggregate(override val aggregateId: Id) : ReservationAggregate() {
    override fun verifyCommand(cmd: ReservationCommand): List<ReservationEvent> {
        return when (cmd) {
            is RequestReservation -> listOf(cmd.toDomainEvent(aggregateId))
            is DeclineReservation -> error("Cannot decline reservation that does not exist")
            is AcceptReservation -> error("Cannot accept reservation that does not exist")
        }
    }

    override fun applyEvent(event: ReservationEvent): ReservationAggregate {
        return when (event) {
            is ReservationRequested -> ReservationRequestedAggregate(
                aggregateId = aggregateId,
                customerId = event.customerId,
                requestId = event.requestId,
                version = Version(),
                startTime = event.startTime,
                endTime = event.endTime
            )

            else -> error("Cannot apply event $event to none reservation")
        }
    }
}

@Serializable
internal sealed class SomeReservationAggregate(val reservationState: ReservationState) :
    ReservationAggregate() {
    abstract val customerId: Id
    abstract val requestId: Id
    abstract val version: Version
    abstract val startTime: Instant
    abstract val endTime: Instant
}

@Serializable
@SerialName("reservation-requested-aggregate")
internal data class ReservationRequestedAggregate(
    override val aggregateId: Id,
    override val customerId: Id,
    override val requestId: Id,
    override val version: Version,
    override val startTime: Instant,
    override val endTime: Instant
) : SomeReservationAggregate(REQUESTED) {
    override fun verifyCommand(cmd: ReservationCommand): List<ReservationEvent> {
        return when (cmd) {
            is RequestReservation -> error("Cannot request reservation that already exists")
            is DeclineReservation -> listOf(cmd.toDomainEvent())
            is AcceptReservation -> listOf(cmd.toDomainEvent())
        }
    }

    override fun applyEvent(event: ReservationEvent): ReservationAggregate {
        return when (event) {
            is ReservationDeclined -> ReservationDeclinedAggregate(
                aggregateId = aggregateId,
                customerId = customerId,
                requestId = requestId,
                version = version.increment(),
                startTime = startTime,
                endTime = endTime
            )

            is ReservationAccepted -> ReservationAcceptedAggregate(
                aggregateId = aggregateId,
                customerId = customerId,
                requestId = requestId,
                version = version.increment(),
                startTime = startTime,
                endTime = endTime,
                timeSlotId = event.timeSlotId
            )

            else -> error("Cannot apply event $event to requested reservation")
        }
    }
}

@Serializable
@SerialName("reservation-declined-aggregate")
internal data class ReservationDeclinedAggregate(
    override val aggregateId: Id,
    override val customerId: Id,
    override val requestId: Id,
    override val version: Version,
    override val startTime: Instant,
    override val endTime: Instant
) : SomeReservationAggregate(DECLINED) {
    override fun verifyCommand(cmd: ReservationCommand): List<ReservationEvent> {
        error("Reservation is declined")
    }

    override fun applyEvent(event: ReservationEvent): ReservationAggregate {
        error("Reservation is declined")
    }
}

@Serializable
@SerialName("reservation-accepted-aggregate")
internal data class ReservationAcceptedAggregate(
    override val aggregateId: Id,
    override val customerId: Id,
    override val requestId: Id,
    override val version: Version,
    override val startTime: Instant,
    override val endTime: Instant,
    val timeSlotId: Id
) : SomeReservationAggregate(ACCEPTED) {
    override fun verifyCommand(cmd: ReservationCommand): List<ReservationEvent> {
        return when (cmd) {
            is RequestReservation -> error("Cannot request reservation that already exists")
            is DeclineReservation -> listOf(cmd.toDomainEvent())
            is AcceptReservation -> error("Cannot accept reservation that is already accepted")
        }
    }

    override fun applyEvent(event: ReservationEvent): ReservationAggregate {
        return when (event) {
            is ReservationDeclined -> ReservationDeclinedAggregate(
                aggregateId = aggregateId,
                customerId = customerId,
                requestId = requestId,
                version = version.increment(),
                startTime = startTime,
                endTime = endTime
            )

            else -> error("Cannot apply event $event to accepted reservation")
        }
    }
}