package com.mz.common.persistence.eventsourcing.aggregate

import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import reactor.core.publisher.Mono

data class CommandEffect<A, E : DomainEvent>(val aggregate: A, val events: List<E>)

interface AggregateRepository<A, C : DomainCommand, E : DomainEvent> {

    fun execute(id: Id, command: C): Mono<CommandEffect<A, E>>

    fun find(id: Id): Mono<A>

}