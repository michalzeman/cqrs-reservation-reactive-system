package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.EventJournalRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.SnapshotAggregateRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.map
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.toEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
internal class CassandraEventStorageAdapter(
    private val eventJournalRepository: EventJournalRepository,
    private val snapshotAggregateRepository: SnapshotAggregateRepository
) : EventStorageAdapter {
    override fun save(eventJournals: List<EventJournal>): Mono<Void> {
        return Mono.fromCallable { eventJournals.map { it.toEntity() } }
            .flatMapMany { eventJournalRepository.saveAll(it) }
            .count()
            .log()
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

    override fun readSnapshot(aggregateId: String): Mono<SnapshotAggregate> {
        TODO("Not yet implemented")
    }

    override fun getEventJournalSequenceNumber(query: SequenceNumberQuery): Mono<Long> {
        return eventJournalRepository.findMaxSequenceNuByAggregateId(query.aggregateId)
    }
}