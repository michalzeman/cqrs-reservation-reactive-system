package com.mz.ddd.common.persistence.eventsourcing.aggregate

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.ddd.common.api.util.Try


internal class AggregateProcessorImpl<A, C : DomainCommand, E : DomainEvent>(
    private val aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
    private val aggregateEventHandler: AggregateEventHandler<A, E>
) : com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessor<A, C, E> {

    override fun execute(
        aggregate: A,
        cmd: C
    ): Try<com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect<A, E>> {
        return aggregateCommandHandler.execute(aggregate, cmd)
            .map { events -> applyEventsToAggregate(aggregate, events) }
    }

    override fun applyEvents(aggregate: A, events: List<E>): A = events
        .fold(aggregate) { acc, event -> aggregateEventHandler.apply(acc, event) }

    private fun applyEventsToAggregate(
        aggregate: A,
        events: List<E>
    ): com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect<A, E> {
        val newAggregate = applyEvents(aggregate, events)
        return com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect(newAggregate, events)
    }
}