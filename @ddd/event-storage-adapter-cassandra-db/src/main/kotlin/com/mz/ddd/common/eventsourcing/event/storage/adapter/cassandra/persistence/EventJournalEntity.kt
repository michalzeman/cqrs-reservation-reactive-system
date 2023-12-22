package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.nio.ByteBuffer
import java.time.Instant

@Table("event_journal")
class EventJournalEntity {
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

fun EventJournalEntity.map(): EventJournal {
    return EventJournal(aggregateId!!, sequenceNr!!, createdAt!!, tag!!, payload!!.array(), payloadType!!)
}

fun EventJournal.toEntity(): EventJournalEntity {
    val self = this
    return EventJournalEntity().apply {
        aggregateId = self.aggregateId
        sequenceNr = sequenceNumber
        createdAt = self.createdAt
        tag = self.tag
        payload = ByteBuffer.wrap(self.payload)
        payloadType = self.payloadType
    }
}