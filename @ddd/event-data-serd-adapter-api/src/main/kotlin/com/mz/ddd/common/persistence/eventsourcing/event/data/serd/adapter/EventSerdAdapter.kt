package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerdAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.Event

interface EventSerdAdapter<E : DomainEvent> {
    fun serialize(event: E): ByteArray

    fun deserialize(event: Event): E

    /**
     * Factory method which returns instance of JsonEventSerdAdapter as EventSerdAdapter.
     */
    companion object {
        operator fun <E : DomainEvent> invoke(
            encode: (event: E) -> ByteArray,
            decode: (event: String) -> E
        ): EventSerdAdapter<E> = JsonEventSerdAdapter(encode, decode)
    }
}