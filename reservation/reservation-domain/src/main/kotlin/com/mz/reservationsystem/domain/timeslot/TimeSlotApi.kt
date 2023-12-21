package com.mz.reservationsystem.domain.timeslot

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotEvent
import com.mz.reservationsystem.domain.timeslot.internal.TimeSlotAggregate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

typealias TimeSlotAggregateManager = AggregateManager<TimeSlotAggregate, TimeSlotCommand, TimeSlotEvent, TimeSlotDocument>

@Component
class TimeSlotApi(
    val timeSlotAggregateManager: TimeSlotAggregateManager
) {
    fun execute(cmd: TimeSlotCommand): Mono<TimeSlotDocument> {
        return timeSlotAggregateManager.execute(cmd, cmd.aggregateId)
    }

    fun findById(id: Id): Mono<TimeSlotDocument> {
        return timeSlotAggregateManager.findById(id)
    }
}