package com.mz.common.persistence.eventsourcing.aggregate

import com.mz.common.persistence.eventsourcing.event.Tag
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.domain.command.AggregateCommandHandler
import com.mz.reservation.common.api.domain.event.AggregateEventHandler
import reactor.core.publisher.Mono

data class AggregateRepositoryBuilder<A, C : DomainCommand, E : DomainEvent, S>(
    val aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
    val aggregateEventHandler: AggregateEventHandler<A, E>,
    val aggregateFactory: (Id) -> A,
    val stateFactory: (A) -> S,
    val domainTag: Tag
)

interface AggregateRepository<A, C : DomainCommand, E : DomainEvent, S> {

    fun execute(id: Id, command: C): Mono<CommandEffect<A, E>>

}