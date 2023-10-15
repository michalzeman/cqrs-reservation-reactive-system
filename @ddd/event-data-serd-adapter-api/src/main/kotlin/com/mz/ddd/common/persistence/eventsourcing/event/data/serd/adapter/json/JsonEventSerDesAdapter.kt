package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter

internal class JsonEventSerDesAdapter<E : DomainEvent>(
    val encode: (event: E) -> ByteArray,
    val decode: (event: String) -> E
) : EventSerDesAdapter<E> {
    override fun serialize(event: E) = encode(event)

    override fun deserialize(eventJournal: EventJournal): E {
        val rawPayload = eventJournal.payload
        return decode(rawPayload.decodeToString())
    }
}