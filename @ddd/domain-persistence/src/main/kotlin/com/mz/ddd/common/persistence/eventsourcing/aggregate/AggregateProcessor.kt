package com.mz.ddd.common.persistence.eventsourcing.aggregate

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.ddd.common.api.util.Try

internal interface AggregateProcessor<A, C : DomainCommand, E : DomainEvent> {
    fun execute(aggregate: A, cmd: C): Try<com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect<A, E>>

    fun applyEvents(aggregate: A, events: List<E>): A

    companion object {
        fun <A, C : DomainCommand, E : DomainEvent> create(
            aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
            aggregateEventHandler: AggregateEventHandler<A, E>
        ): com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessor<A, C, E> =
            com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessorImpl(
                aggregateCommandHandler,
                aggregateEventHandler
            )
    }
}
