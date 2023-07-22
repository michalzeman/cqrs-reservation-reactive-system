package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerdAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.Event

internal class JsonEventSerdAdapter<E : DomainEvent>(
    val encode: (event: E) -> ByteArray,
    val decode: (event: String) -> E
) : EventSerdAdapter<E> {
    override fun serialize(event: E) = encode(event)

    override fun deserialize(event: Event): E {
        val rawPayload = event.payload
        return decode(rawPayload.decodeToString())
    }
}