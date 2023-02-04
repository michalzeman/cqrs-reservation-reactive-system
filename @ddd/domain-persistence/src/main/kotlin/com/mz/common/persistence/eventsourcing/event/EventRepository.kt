package com.mz.common.persistence.eventsourcing.event

import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.domain.validatedString
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

data class Tag(val value: String) {
    init {
        validatedString(value)
    }
}

/**
 * Event data.
 */
data class Event(
    val id: String,
    val sequenceNumber: Long,
    val createdAt: Instant,
    val tag: String,
    val payload: String,
    val payloadType: String
)

interface EventRepository<E : DomainEvent> {

    fun persistAll(id: Id, events: List<E>, tag: Tag): Mono<Void>

    fun read(id: Id, tag: Tag): Flux<Event>

}

