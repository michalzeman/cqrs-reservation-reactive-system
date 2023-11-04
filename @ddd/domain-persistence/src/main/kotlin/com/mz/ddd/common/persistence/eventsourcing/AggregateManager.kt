package com.mz.ddd.common.persistence.eventsourcing

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import reactor.core.publisher.Mono

/**
 * Domain facade contract for operations related with state management of Domain entity (Aggregate root).
 * @param A - Domain Entity type (Root Aggregate)
 * @param C - Domain command type -> mutation of Domain entity
 * @param S - State type, represents state/snapshot/document of domain entity
 */
interface AggregateManager<A, C : DomainCommand, E : DomainEvent, S> {

    /**
     * Apply changes to Domain entity.
     * @param command - Command for mutating of Domain entity
     * @param id - Id of the domain entity
     */
    fun execute(command: C, id: Id): Mono<S>

    fun executeAndReturnEvents(command: C, id: Id): Mono<List<E>>

    fun findById(id: Id): Mono<S>
}