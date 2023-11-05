package com.mz.ddd.common.persistence.eventsourcing.aggregate

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import reactor.core.publisher.Mono

data class CommandEffect<A : Aggregate, E : DomainEvent>(val aggregate: A, val events: List<E>)

interface AggregateRepository<A : Aggregate, C : DomainCommand, E : DomainEvent> {

    fun execute(id: Id, command: C): Mono<CommandEffect<A, E>>

    fun find(id: Id): Mono<A>

}