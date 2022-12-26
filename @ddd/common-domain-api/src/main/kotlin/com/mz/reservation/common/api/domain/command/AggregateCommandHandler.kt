package com.mz.reservation.common.api.domain.command

import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.util.Try

interface AggregateCommandHandler<A, C : DomainCommand, E : DomainEvent> {

    fun execute(aggregate: A, command: C): Try<List<E>>

}