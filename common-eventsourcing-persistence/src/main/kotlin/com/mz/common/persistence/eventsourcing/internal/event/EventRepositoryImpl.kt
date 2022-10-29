package com.mz.common.persistence.eventsourcing.internal.event

import com.mz.common.persistence.eventsourcing.event.Event
import com.mz.common.persistence.eventsourcing.event.EventRepository
import com.mz.common.persistence.eventsourcing.event.EventStorageAdapter
import com.mz.common.persistence.eventsourcing.event.Tag
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

internal class EventRepositoryImpl<E : DomainEvent>(private val eventStorageAdapter: EventStorageAdapter) :
    EventRepository<E> {

    override fun persistAll(id: Id, events: List<E>, tag: Tag): Mono<Void> {

        fun mapEvent(sequenceNumber: Long, event: E): Event = Event(
            persistenceId = id.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            tag = tag.value,
            payload = event.toString(),
            payloadType = event.javaClass.typeName
        )

        return eventStorageAdapter.getSequenceNumber(id.value)
            .map { sequenceN ->
                events.mapIndexed { index, event -> mapEvent(sequenceN + 1 + index, event) }
            }
            .flatMap(eventStorageAdapter::save)
    }

    override fun read(id: Id, tag: Tag): Flux<Event> {
        return eventStorageAdapter.read(id.value, tag.value)
    }

}