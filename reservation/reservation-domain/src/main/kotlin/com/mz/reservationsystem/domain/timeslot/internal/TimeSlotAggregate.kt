package com.mz.reservationsystem.domain.timeslot.internal

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCreated
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotEvent
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotUpdated
import com.mz.reservationsystem.domain.api.timeslot.UpdateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.toEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantPast
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TimeSlotAggregate : Aggregate() {
    abstract fun apply(event: TimeSlotEvent): TimeSlotAggregate

    abstract fun verify(cmd: TimeSlotCommand): List<TimeSlotEvent>
}

@Serializable
@SerialName("none-time-slot-aggregate")
data class NoneTimeSlotAggregate(override val aggregateId: Id) : TimeSlotAggregate() {

    override fun verify(cmd: TimeSlotCommand): List<TimeSlotEvent> {
        return when (cmd) {
            is CreateTimeSlot -> {
                validateDates(cmd.endTime, cmd.startTime)
                listOf(cmd.toEvent())
            }

            is UpdateTimeSlot -> error("Wrong command type ${cmd::class} for the none time slot aggregate")
        }
    }

    override fun apply(event: TimeSlotEvent): SomeTimeSlotAggregate {
        return when (event) {
            is TimeSlotCreated -> SomeTimeSlotAggregate(
                aggregateId = event.aggregateId,
                version = Version(),
                startTime = event.startTime,
                endTime = event.endTime,
                booked = event.booked,
                valid = true
            )

            is TimeSlotUpdated -> error("Cannot apply event ${event::class} to none time slot aggregate")
        }
    }
}


@Serializable
@SerialName("some-time-slot-aggregate")
data class SomeTimeSlotAggregate(
    override val aggregateId: Id,
    val version: Version,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    val reservationId: Id? = null
) : TimeSlotAggregate() {

    override fun verify(cmd: TimeSlotCommand): List<TimeSlotEvent> {
        return when (cmd) {
            is UpdateTimeSlot -> {
                cmd.validateUpdate()
                listOf(cmd.toEvent())
            }

            is CreateTimeSlot -> error("Wrong command type ${cmd::class} for the some time slot aggregate")
        }
    }

    override fun apply(event: TimeSlotEvent): SomeTimeSlotAggregate {
        return when (event) {
            is TimeSlotCreated -> error("Cannot apply event ${event::class} to some time slot aggregate")
            is TimeSlotUpdated -> this.copy(
                version = version.increment(),
                startTime = event.startTime,
                endTime = event.endTime,
                booked = event.booked,
                valid = event.valid,
                reservationId = event.reservationId
            )
        }
    }
}

internal fun validateDates(endTime: Instant, startTime: Instant) {
    if (startTime.isDistantPast && endTime.isDistantPast) {
        error("Start time and end time cannot be in the past")
    } else if (startTime.epochSeconds > endTime.epochSeconds) {
        error("End time cannot be before start time")
    }
}

internal fun UpdateTimeSlot.validateUpdate() {
    validateDates(endTime, startTime)
    if (booked && reservationId == null) {
        error("Reservation id cannot be null if booked is true")
    }
}
