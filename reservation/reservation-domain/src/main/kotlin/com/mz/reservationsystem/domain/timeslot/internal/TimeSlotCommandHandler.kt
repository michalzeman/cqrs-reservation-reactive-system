package com.mz.reservationsystem.domain.timeslot.internal

import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotEvent

class TimeSlotCommandHandler : AggregateCommandHandler<TimeSlotAggregate, TimeSlotCommand, TimeSlotEvent> {
    override fun execute(aggregate: TimeSlotAggregate, command: TimeSlotCommand): Result<List<TimeSlotEvent>> {
        return Result.runCatching { aggregate.verify(command) }
    }
}
