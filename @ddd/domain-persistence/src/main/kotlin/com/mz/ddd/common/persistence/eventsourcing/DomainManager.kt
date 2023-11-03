package com.mz.ddd.common.persistence.eventsourcing

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import reactor.core.publisher.Mono

/**
 * Domain facade contract for operations related with state management of Domain entity (Aggregate root).
 * @param A - Domain Entity type (Root Aggregate)
 * @param C - Domain command type -> mutation of Domain entity
 * @param S - State type, represents state/snapshot/document of domain entity
 */
interface DomainManager<A, C : DomainCommand, E : DomainEvent, S> {

    /**
     * Apply changes to Domain entity.
     * @param command - Command for mutating of Domain entity
     * @param id - Id of the domain entity
     */
    fun execute(command: C, id: String): Mono<S>

    fun executeAndReturnEvents(command: C, id: String): Mono<List<E>>

    fun findById(id: String): Mono<S>
}