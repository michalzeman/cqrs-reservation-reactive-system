package com.mz.ddd.common.persistence.eventsourcing.event

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerdAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.Event
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.EventStorageAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.SequenceNumberQuery
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

internal class EventRepositoryImpl<E : DomainEvent>(
    private val domainTag: DomainTag,
    private val eventStorageAdapter: EventStorageAdapter,
    private val eventSerdAdapter: EventSerdAdapter<E>
) : EventRepository<E> {

    override fun persistAll(id: Id, events: List<E>): Mono<Void> {

        fun mapEvent(sequenceNumber: Long, event: E): Event = Event(
            id = id.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            tag = domainTag.value,
            payload = eventSerdAdapter.serialize(event),
            payloadType = event.javaClass.typeName
        )

        return eventStorageAdapter.getSequenceNumber(SequenceNumberQuery(id = id.value, tag = domainTag.value))
            .map { sequenceN ->
                events.mapIndexed { index, event -> mapEvent(sequenceN + 1 + index, event) }
            }
            .flatMap(eventStorageAdapter::save)
    }

    override fun read(id: Id): Flux<E> = eventStorageAdapter
        .read(id.value)
        .map(eventSerdAdapter::deserialize)

}