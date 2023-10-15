package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
internal interface SnapshotAggregateRepository : ReactiveCassandraRepository<SnapshotAggregateEntity, String> {
    fun findByAggregateId(
        aggregateId: String,
        sort: Sort = Sort.by(Sort.Direction.DESC, "sequenceNr")
    ): Flux<SnapshotAggregateEntity>

    fun findByAggregateIdAndSequenceNrGreaterThanEqual(
        aggregateId: String,
        sequenceNr: Long,
        sort: Sort = Sort.by(Sort.Direction.DESC, "sequenceNr")
    ): Flux<SnapshotAggregateEntity>
}