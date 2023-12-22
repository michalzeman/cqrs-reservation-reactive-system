package com.mz.reservationsystem.domain.timeslot

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.reservationsystem.domain.api.timeslot.BookTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotEvent
import com.mz.reservationsystem.domain.api.timeslot.UpdateTimeSlot
import com.mz.reservationsystem.domain.internal.timeslot.TimeSlotAggregate
import kotlinx.datetime.Instant
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

typealias TimeSlotAggregateManager = AggregateManager<TimeSlotAggregate, TimeSlotCommand, TimeSlotEvent, TimeSlotDocument>

@Component
class TimeSlotApi(
    @Qualifier("timeSlotAggregateManager") val timeSlotAggregateManager: TimeSlotAggregateManager,
    val timeSlotView: TimeSlotView
) {
    fun execute(cmd: TimeSlotCommand): Mono<TimeSlotDocument> {
        return when (cmd) {
            is CreateTimeSlot -> timeSlotAggregateManager.execute(
                cmd,
                cmd.aggregateId
            ) { findTimeSlotBetweenTimes(cmd.startTime, cmd.endTime) }

            is UpdateTimeSlot -> timeSlotAggregateManager.execute(
                cmd,
                cmd.aggregateId
            ) { findTimeSlotBetweenTimes(cmd.startTime, cmd.endTime) }

            is BookTimeSlot -> timeSlotAggregateManager.execute(
                cmd,
                cmd.aggregateId
            )
        }
    }

    fun findById(id: Id): Mono<TimeSlotDocument> {
        return timeSlotAggregateManager.findById(id)
    }

    private fun findTimeSlotBetweenTimes(
        startTime: Instant,
        endTime: Instant
    ): Mono<Boolean> {
        return timeSlotView.find(
            FindTimeSlotBetweenTimes(
                startTime = startTime,
                endTime = endTime
            )
        ).count()
            .map { count ->
                if (count > 0) error("Time slot already exist")
                else true
            }
    }
}