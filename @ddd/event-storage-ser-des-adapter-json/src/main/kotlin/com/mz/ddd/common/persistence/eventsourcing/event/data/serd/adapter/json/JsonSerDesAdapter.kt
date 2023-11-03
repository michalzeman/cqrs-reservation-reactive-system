package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.SerDesAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias Encoder<E> = (value: E) -> ByteArray
typealias Decode<E> = (value: String) -> E

abstract class JsonSerDesAdapter<E>(
    val encode: Encoder<E>,
    val decode: Decode<E>
) : SerDesAdapter {
    override val contentType: String = "application/json"
}

/**
 * Kotlin native supported JSON serialization
 */
inline fun <reified T : DomainEvent> serToJsonString(value: T) = Json.encodeToString<T>(value).encodeToByteArray()

/**
 * Kotlin native supported JSON deserialization
 */
inline fun <reified T : DomainEvent> desJson(value: String): T = Json.decodeFromString<T>(value)
