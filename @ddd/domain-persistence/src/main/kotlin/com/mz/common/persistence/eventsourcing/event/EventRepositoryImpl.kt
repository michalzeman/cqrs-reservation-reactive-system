package com.mz.common.persistence.eventsourcing.event

import com.mz.common.persistence.eventsourcing.event.storage.adapter.Event
import com.mz.common.persistence.eventsourcing.event.storage.adapter.EventStorageAdapter
import com.mz.common.persistence.eventsourcing.event.storage.adapter.SequenceNumberQuery
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

internal class EventRepositoryImpl<E : DomainEvent>(
    private val tag: Tag,
    private val eventStorageAdapter: EventStorageAdapter,
    private val eventSerdAdapter: EventSerdAdapter<E>
) : EventRepository<E> {

    override fun persistAll(id: Id, events: List<E>): Mono<Void> {

        fun mapEvent(sequenceNumber: Long, event: E): Event = Event(
            id = id.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            tag = tag.value,
            payload = eventSerdAdapter.serialize(event),
            payloadType = event.javaClass.typeName
        )

        return eventStorageAdapter.getSequenceNumber(SequenceNumberQuery(id = id.value, tag = tag.value))
            .map { sequenceN ->
                events.mapIndexed { index, event -> mapEvent(sequenceN + 1 + index, event) }
            }
            .flatMap(eventStorageAdapter::save)
    }

    override fun read(id: Id): Flux<E> = eventStorageAdapter
        .read(id.value)
        .map(eventSerdAdapter::deserialize)

}