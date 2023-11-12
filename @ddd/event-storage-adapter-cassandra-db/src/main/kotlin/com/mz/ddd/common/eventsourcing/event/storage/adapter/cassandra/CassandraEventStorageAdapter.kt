package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.EventJournalRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.SnapshotRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.map
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.toEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Component
internal class CassandraEventStorageAdapter(
    private val eventJournalRepository: EventJournalRepository,
    private val snapshotRepository: SnapshotRepository
) : EventStorageAdapter {

    override fun save(eventJournals: List<EventJournal>): Mono<Long> {
        val lastSequence = eventJournals.maxBy { it.sequenceNumber }.sequenceNumber
        return eventJournals.toMono()
            .map { events -> events.map { it.toEntity() } }
            .flatMapMany { eventJournalRepository.saveAll(it) }
            .then(lastSequence.toMono())
            .log()
    }

    override fun save(snapshot: Snapshot): Mono<Void> {
        return snapshot.toMono()
            .map { it.toEntity() }
            .flatMap { snapshotRepository.save(it) }
            .then()
    }

    override fun readEvents(aggregateId: String, sequence: Long?): Flux<EventJournal> {
        val result = if (sequence != null) eventJournalRepository.findByAggregateIdAndSequenceNrGreaterThanEqual(
            aggregateId,
            sequence
        )
        else eventJournalRepository.findByAggregateId(aggregateId)

        return result.map { it.map() }
    }

    override fun readSnapshot(aggregateId: String): Mono<Snapshot> {
        return snapshotRepository.findByAggregateId(aggregateId)
            .take(1)
            .singleOrEmpty()
            .flatMap { snapshot ->
                eventJournalRepository.findByAggregateIdAndSequenceNrGreaterThanEqual(
                    snapshot.aggregateId!!,
                    snapshot.sequenceNr!! + 1
                )
                    .collectList()
                    .map { snapshot.map(it) }
            }
    }

    override fun getEventJournalSequenceNumber(query: SequenceNumberQuery): Mono<Long> {
        return eventJournalRepository.findMaxSequenceNuByAggregateId(query.aggregateId)
            .switchIfEmpty { Mono.just(0L) }
    }

    override fun countOfEventsCreatedAfterLastSnapshot(query: SequenceNumberQuery): Mono<Long> {
        return snapshotRepository.findByAggregateId(query.aggregateId)
            .take(1)
            .singleOrEmpty()
            .flatMap { snapshot ->
                eventJournalRepository.countByAggregateIdAndSequenceNrGreaterThan(
                    snapshot.aggregateId!!,
                    snapshot.sequenceNr!!
                )
            }
            .switchIfEmpty { eventJournalRepository.countByAggregateId(query.aggregateId) }
    }
}
