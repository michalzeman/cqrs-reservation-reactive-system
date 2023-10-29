package com.mz.ddd.common.persistence.eventsourcing.event

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.validateNotBlank
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

interface EventRepository<E : DomainEvent> {

    fun persistAll(id: Id, events: List<E>): Mono<Void>

    fun read(id: Id): Flux<E>

}

