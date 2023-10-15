package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.SnapshotAggregate
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.nio.ByteBuffer
import java.time.Instant

@Table("snapshot_aggregate")
class SnapshotAggregateEntity {
    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.PARTITIONED)
    var aggregateId: String? = null

    @PrimaryKeyColumn(name = "sequence_nr", type = PrimaryKeyType.PARTITIONED)
    var sequenceNr: Long? = null

    @Column("created_at")
    var createdAt: Instant? = null

    var tag: String? = null

    var payload: ByteBuffer? = null

    @Column("payload_type")
    var payloadType: String? = null
}

fun SnapshotAggregateEntity.map(eventJournals: List<EventJournalEntity> = listOf()): SnapshotAggregate {
    return SnapshotAggregate(
        aggregateId!!,
        sequenceNr!!,
        createdAt!!,
        tag!!,
        payload!!.array(),
        payloadType!!,
        eventJournals.map { it.map() })
}

fun SnapshotAggregate.toEntity(): SnapshotAggregateEntity {
    val self = this
    return SnapshotAggregateEntity().apply {
        aggregateId = self.aggregateId
        sequenceNr = sequenceNumber
        createdAt = self.createdAt
        tag = self.tag
        payload = ByteBuffer.wrap(self.payload)
        payloadType = self.payloadType
    }
}