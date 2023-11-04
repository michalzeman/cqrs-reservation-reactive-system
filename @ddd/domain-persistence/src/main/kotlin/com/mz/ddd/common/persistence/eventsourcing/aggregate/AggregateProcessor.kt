package com.mz.ddd.common.persistence.eventsourcing.aggregate

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.ddd.common.api.domain.event.AggregateEventHandler

interface AggregateProcessor<A : Aggregate, C : DomainCommand, E : DomainEvent> {
    fun execute(aggregate: A, cmd: C): Result<CommandEffect<A, E>>

    fun applyEvents(aggregate: A, events: List<E>): A

    companion object {
        operator fun <A : Aggregate, C : DomainCommand, E : DomainEvent> invoke(
            aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
            aggregateEventHandler: AggregateEventHandler<A, E>
        ): AggregateProcessor<A, C, E> =
            AggregateProcessorImpl(
                aggregateCommandHandler,
                aggregateEventHandler
            )
    }
}
