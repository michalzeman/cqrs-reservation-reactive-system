package com.mz.common.persistence.eventsourcing.aggregate

import com.mz.common.persistence.eventsourcing.aggregate.internal.AggregateProcessorImpl
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.command.AggregateCommandHandler
import com.mz.reservation.common.api.domain.event.AggregateEventHandler
import com.mz.reservation.common.api.util.Try

data class CommandEffect<A, E : DomainEvent>(val aggregate: A, val events: List<E>)

interface AggregateProcessor<A, C : DomainCommand, E : DomainEvent> {
    fun execute(aggregate: A, cmd: C): Try<CommandEffect<A, E>>

    fun applyEvents(aggregate: A, events: List<E>): A

    companion object {
        fun <A, C : DomainCommand, E : DomainEvent> create(
            aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
            aggregateEventHandler: AggregateEventHandler<A, E>
        ): AggregateProcessor<A, C, E> =
            AggregateProcessorImpl(aggregateCommandHandler, aggregateEventHandler)
    }
}
