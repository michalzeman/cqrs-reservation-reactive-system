package com.mz.common.persistence.eventsourcing.aggregate

import com.mz.common.persistence.eventsourcing.event.Tag
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.domain.command.AggregateCommandHandler
import com.mz.reservation.common.api.domain.event.AggregateEventHandler
import reactor.core.publisher.Mono

data class AggregateRepositoryBuilder<A, C : DomainCommand, E : DomainEvent, S>(
    val aggregateCommandHandler: AggregateCommandHandler<A, C, DomainEvent>,
    val aggregateEventHandler: AggregateEventHandler<A, E>,
    val aggregateFactory: (Id) -> A,
    val stateFactory: (A) -> S,
    val domainTag: Tag
)

data class CommandEffect<A, E : DomainEvent>(val aggregate: A, val events: List<E>)

interface AggregateRepository<A, C : DomainCommand, E : DomainEvent> {

    fun execute(id: Id, command: C): Mono<CommandEffect<A, E>>

    fun find(id: Id): Mono<A>

}