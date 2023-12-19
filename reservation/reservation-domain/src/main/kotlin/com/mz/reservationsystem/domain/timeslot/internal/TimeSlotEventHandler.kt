package com.mz.reservationsystem.domain.timeslot.internal

import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotEvent

class TimeSlotEventHandler : AggregateEventHandler<TimeSlotAggregate, TimeSlotEvent> {
    override fun apply(aggregate: TimeSlotAggregate, event: TimeSlotEvent): TimeSlotAggregate {
        return aggregate.apply(event)
    }
}