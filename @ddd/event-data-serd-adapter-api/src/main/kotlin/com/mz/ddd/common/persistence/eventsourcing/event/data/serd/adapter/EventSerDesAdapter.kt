package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.EventJournal

interface EventSerDesAdapter<E : DomainEvent> {
    fun serialize(event: E): ByteArray

    fun deserialize(eventJournal: EventJournal): E

    /**
     * Factory method which returns instance of JsonEventSerDesAdapter as EventSerDesAdapter.
     */
    companion object {
        operator fun <E : DomainEvent> invoke(
            encode: (event: E) -> ByteArray,
            decode: (event: String) -> E
        ): EventSerDesAdapter<E> = JsonEventSerDesAdapter(encode, decode)
    }
}