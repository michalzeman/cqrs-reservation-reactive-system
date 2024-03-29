package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal interface EventJournalRepository : ReactiveCassandraRepository<EventJournalEntity, String> {
    fun findByAggregateId(
        aggregateId: String,
        sort: Sort = Sort.by(Sort.Direction.ASC, "sequenceNr")
    ): Flux<EventJournalEntity>

    fun countByAggregateId(aggregateId: String): Mono<Long>

    fun findByAggregateIdAndSequenceNrGreaterThanEqual(
        aggregateId: String,
        sequenceNr: Long,
        sort: Sort = Sort.by(Sort.Direction.ASC, "sequenceNr")
    ): Flux<EventJournalEntity>

    fun countByAggregateIdAndSequenceNrGreaterThan(
        aggregateId: String,
        sequenceNr: Long
    ): Mono<Long>

    @Query("select max(sequence_nr) from event_journal where aggregate_id=:aggregateId")
    fun findMaxSequenceNuByAggregateId(@Param("aggregateId") aggregateId: String): Mono<Long>
}