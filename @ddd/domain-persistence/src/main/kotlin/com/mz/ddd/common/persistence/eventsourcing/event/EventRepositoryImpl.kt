package com.mz.ddd.common.persistence.eventsourcing.event

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventStorageAdapter
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.SequenceNumberQuery
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

internal class EventRepositoryImpl<E : DomainEvent, S : Document<E>>(
    private val domainTag: DomainTag,
    private val eventStorageAdapter: EventStorageAdapter,
    private val eventSerDesAdapter: EventSerDesAdapter<E>
) : EventRepository<E, S> {

    override fun persistAll(id: Id, events: List<E>): Mono<Void> {

        fun mapEvent(sequenceNumber: Long, event: E): EventJournal = EventJournal(
            aggregateId = id.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            tag = domainTag.value,
            payload = eventSerDesAdapter.serialize(event),
            payloadType = eventSerDesAdapter.contentType
        )

        return eventStorageAdapter.getEventJournalSequenceNumber(
            SequenceNumberQuery(
                aggregateId = id.value,
                tag = domainTag.value
            )
        )
            .map { it + 1 }
            .map { sequenceN ->
                events.mapIndexed { index, event -> mapEvent(sequenceN + index, event) }
            }
            .flatMap(eventStorageAdapter::save)
            .then()
    }

    override fun read(id: Id): Flux<E> = eventStorageAdapter
        .readEvents(id.value)
        .map(eventSerDesAdapter::deserialize)

}