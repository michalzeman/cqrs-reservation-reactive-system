package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot

/**
 * EventJournal serialization/deserialization contract.
 */
interface EventSerDesAdapter<E : DomainEvent, S : Aggregate> {

    val contentType: String

    fun serialize(event: E): ByteArray

    fun serialize(aggregate: S): ByteArray

    fun deserialize(eventJournal: EventJournal): E

    fun deserialize(snapshot: Snapshot): S

}