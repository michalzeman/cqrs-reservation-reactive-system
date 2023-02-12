package com.mz.common.persistence.eventsourcing.event

import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.domain.validatedString
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class Tag(val value: String) {
    init {
        validatedString(value)
    }
}

interface EventRepository<E : DomainEvent> {

    fun persistAll(id: Id, events: List<E>): Mono<Void>

    fun read(id: Id): Flux<E>

}

