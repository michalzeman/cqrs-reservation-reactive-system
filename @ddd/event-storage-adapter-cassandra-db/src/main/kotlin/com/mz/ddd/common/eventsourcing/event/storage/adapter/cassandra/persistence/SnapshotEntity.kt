package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.nio.ByteBuffer
import java.time.Instant

@Table("snapshot")
class SnapshotEntity {
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

fun SnapshotEntity.map(eventJournals: List<EventJournalEntity> = listOf()): Snapshot {
    return Snapshot(
        aggregateId!!,
        sequenceNr!!,
        createdAt!!,
        tag!!,
        payload!!.array(),
        payloadType!!,
        eventJournals.map { it.map() })
}

fun Snapshot.toEntity(): SnapshotEntity {
    val self = this
    return SnapshotEntity().apply {
        aggregateId = self.aggregateId
        sequenceNr = sequenceNumber
        createdAt = self.createdAt
        tag = self.tag
        payload = ByteBuffer.wrap(self.payload)
        payloadType = self.payloadType
    }
}