package com.mz.common.persistence.eventsourcing.event

import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

internal class EventRepositoryImpl<E : DomainEvent>(
    private val eventStorageAdapter: EventStorageAdapter,
    private val eventSerDeSerAdapter: EventSerDeSerAdapter<E>
) : EventRepository<E> {

    override fun persistAll(id: Id, events: List<E>): Mono<Void> {

        fun mapEvent(sequenceNumber: Long, event: E): Event = Event(
            id = id.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            payload = eventSerDeSerAdapter.serialize(event),
            payloadType = event.javaClass.typeName
        )

        return eventStorageAdapter.getSequenceNumber(id.value)
            .map { sequenceN ->
                events.mapIndexed { index, event -> mapEvent(sequenceN + 1 + index, event) }
            }
            .flatMap(eventStorageAdapter::save)
    }

    override fun read(id: Id): Flux<E> = eventStorageAdapter
        .read(id.value)
        .map(eventSerDeSerAdapter::deserialize)

}