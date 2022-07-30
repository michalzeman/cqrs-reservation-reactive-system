package com.mz.common.persistence.eventsourcing.aggregate.internal

import com.mz.common.persistence.eventsourcing.aggregate.AggregateProcessor
import com.mz.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.command.AggregateCommandHandler
import com.mz.reservation.common.api.domain.event.AggregateEventHandler
import com.mz.reservation.common.api.util.Try


internal class AggregateProcessorImpl<A, C : DomainCommand, E : DomainEvent>(
    private val aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
    private val aggregateEventHandler: AggregateEventHandler<A, E>
) : AggregateProcessor<A, C, E> {

    override fun execute(aggregate: A, cmd: C): Try<CommandEffect<A, E>> {
        return aggregateCommandHandler.execute(aggregate, cmd)
            .map { events -> applyEventsToAggregate(aggregate, events) }
    }

    override fun applyEvents(aggregate: A, events: List<E>): A = events
        .fold(aggregate) { acc, event -> aggregateEventHandler.apply(acc, event) }

    private fun applyEventsToAggregate(aggregate: A, events: List<E>): CommandEffect<A, E> {
        val newAggregate = applyEvents(aggregate, events)
        return CommandEffect(newAggregate, events)
    }
}