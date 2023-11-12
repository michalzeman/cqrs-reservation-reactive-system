package com.mz.ddd.common.persistence.eventsourcing.event

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.validateNotBlank
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Domain entity related tag, usually it is some Aggregate.
 */
data class DomainTag(val value: String) {
    init {
        value.validateNotBlank()
    }
}

interface EventRepository<E : DomainEvent, A : Aggregate> {

    fun persistAll(id: Id, events: List<E>): Mono<Void>
    fun persistAll(effect: CommandEffect<A, E>): Mono<Void>

    fun read(id: Id): Flux<E>

    fun readSnapshot(id: Id): Mono<Snapshot>
}

