package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias Encoder<E> = (value: E) -> ByteArray
typealias Decoder<E> = (value: String) -> E

/**
 * JSON event, snapshot serialization/deserialization adapter.
 */
class JsonEventSerDesAdapter<E : DomainEvent, A : Aggregate>(
    val encodeEvent: Encoder<E>,
    val decoderEvent: Decoder<E>,
    val encodeAggregate: Encoder<A>,
    val decoderAggregate: Decoder<A>
) : EventSerDesAdapter<E, A> {

    companion object {
        inline fun <reified E : DomainEvent, reified A : Aggregate> build(): EventSerDesAdapter<E, A> {
            return JsonEventSerDesAdapter(
                { serToJsonEncodedBytes(it) },
                { desJson(it) },
                { serToJsonEncodedBytes(it) },
                { desJson(it) }
            )
        }
    }

    override val contentType: String = "application/json"
    override fun serialize(aggregate: A): ByteArray {
        return encodeAggregate(aggregate)
    }

    override fun serialize(event: E) = encodeEvent(event)

    override fun deserialize(eventJournal: EventJournal): E {
        val rawPayload = eventJournal.payload
        return decoderEvent(rawPayload.decodeToString())
    }

    override fun deserialize(snapshot: Snapshot): A {
        val rawPayload = snapshot.payload
        return decoderAggregate(rawPayload.decodeToString())
    }
}

/**
 * Kotlin native supported JSON serialization
 */
inline fun <reified T> serToJsonEncodedBytes(value: T): ByteArray = Json.encodeToString<T>(value).encodeToByteArray()

/**
 * Kotlin native supported JSON deserialization
 */
inline fun <reified T> desJson(value: String): T = Json.decodeFromString<T>(value)
