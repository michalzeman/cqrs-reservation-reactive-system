package com.mz.reservationsystem.domain.internal.reservation

import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.reservationsystem.domain.api.reservation.ReservationCommand
import com.mz.reservationsystem.domain.api.reservation.ReservationEvent

class ReservationCommandHandler : AggregateCommandHandler<ReservationAggregate, ReservationCommand, ReservationEvent> {
    override fun execute(aggregate: ReservationAggregate, command: ReservationCommand): Result<List<ReservationEvent>> {
        return Result.runCatching { aggregate.verifyCommand(command) }
    }
}