package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal

/**
 * EventJournal serialization/deserialization contract.
 */
interface EventSerDesAdapter<E : DomainEvent> : SerDesAdapter {

    fun serialize(event: E): ByteArray

    fun deserialize(eventJournal: EventJournal): E

}