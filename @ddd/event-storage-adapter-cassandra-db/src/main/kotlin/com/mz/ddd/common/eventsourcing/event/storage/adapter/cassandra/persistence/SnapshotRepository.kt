package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
internal interface SnapshotRepository : ReactiveCassandraRepository<SnapshotEntity, String> {
    fun findByAggregateId(
        aggregateId: String,
        sort: Sort = Sort.by(Sort.Direction.DESC, "sequenceNr")
    ): Flux<SnapshotEntity>

    fun findByAggregateIdAndSequenceNrGreaterThanEqual(
        aggregateId: String,
        sequenceNr: Long,
        sort: Sort = Sort.by(Sort.Direction.DESC, "sequenceNr")
    ): Flux<SnapshotEntity>
}