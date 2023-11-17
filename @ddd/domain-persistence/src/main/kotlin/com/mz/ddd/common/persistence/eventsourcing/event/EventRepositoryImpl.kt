package com.mz.ddd.common.persistence.eventsourcing.event

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventStorageAdapter
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.SequenceNumberQuery
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import com.mz.ddd.common.persistence.eventsourcing.DomainPersistenceProperties
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

internal class EventRepositoryImpl<E : DomainEvent, A : Aggregate>(
    private val domainTag: DomainTag,
    private val eventStorageAdapter: EventStorageAdapter,
    private val serDesAdapter: EventSerDesAdapter<E, A>,
    private val properties: DomainPersistenceProperties
) : EventRepository<E, A> {

    override fun persistAll(id: Id, events: List<E>): Mono<Void> {
        return persistAllEventsAndReturnLastSeqNumber(id, events)
            .then()
    }

    override fun persistAll(effect: CommandEffect<A, E>): Mono<Void> {
        val eventsCountAfterSnapshotCreated = eventStorageAdapter.countOfEventsCreatedAfterLastSnapshot(
            SequenceNumberQuery(
                effect.aggregate.aggregateId.value,
                domainTag.value
            )
        ).cache()

        return eventsCountAfterSnapshotCreated.map { it + effect.events.size >= properties.eventsPerSnapshot }
            .flatMap { newSnapshotNeeded ->
                if (newSnapshotNeeded) {
                    eventsCountAfterSnapshotCreated.flatMap {
                        createNewSnapshotAndPersistsEvents(effect)
                    }
                } else {
                    persistAll(effect.aggregate.aggregateId, effect.events)
                }
            }
    }

    override fun readSnapshot(id: Id): Mono<Snapshot> {
        return eventStorageAdapter.readSnapshot(id.value)
    }

    override fun read(id: Id): Flux<E> = eventStorageAdapter
        .readEvents(id.value)
        .map(serDesAdapter::deserialize)

    private fun createNewSnapshotAndPersistsEvents(
        effect: CommandEffect<A, E>
    ): Mono<Void> {
        fun mapToSnapshot(sequenceNumber: Long, aggregate: A): Snapshot = Snapshot(
            aggregateId = aggregate.aggregateId.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            tag = domainTag.value,
            payload = serDesAdapter.serialize(aggregate),
            payloadType = serDesAdapter.contentType
        )

        return persistAllEventsAndReturnLastSeqNumber(effect.aggregate.aggregateId, effect.events)
            .flatMap { eventStorageAdapter.save(mapToSnapshot(it, effect.aggregate)) }
    }

    private fun persistAllEventsAndReturnLastSeqNumber(id: Id, events: List<E>): Mono<Long> {
        fun mapEvent(sequenceNumber: Long, event: E): EventJournal = EventJournal(
            aggregateId = id.value,
            sequenceNumber = sequenceNumber,
            createdAt = Instant.now(),
            tag = domainTag.value,
            payload = serDesAdapter.serialize(event),
            payloadType = serDesAdapter.contentType
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
    }
}