package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * JSON event serialization/deserialization adapter.
 */
class JsonEventSerDesAdapter<E : DomainEvent>(
    val encode: (event: E) -> ByteArray,
    val decode: (event: String) -> E
) : EventSerDesAdapter<E> {

    override fun contentType(): String = "application/json"
    override fun serialize(event: E) = encode(event)

    override fun deserialize(eventJournal: EventJournal): E {
        val rawPayload = eventJournal.payload
        return decode(rawPayload.decodeToString())
    }
}

/**
 * Kotlin native supported JSON serialization
 */
inline fun <reified T : DomainEvent> serToJsonString(value: T) = Json.encodeToString<T>(value).encodeToByteArray()

/**
 * Kotlin native supported JSON deserialization
 */
inline fun <reified T : DomainEvent> desJson(value: String): T = Json.decodeFromString<T>(value)