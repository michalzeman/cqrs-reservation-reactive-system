package com.mz.common.persistence.eventsourcing.event.storage.adapter

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

data class SequenceNumberQuery(val id: String, val tag: String)

/**
 * Event data.
 */
data class Event(
    val id: String,
    val sequenceNumber: Long,
    val createdAt: Instant,
    val tag: String,
    val payload: ByteArray,
    val payloadType: String
)

/**
 * Event storage adapter contract.
 */
interface EventStorageAdapter {

    /**
     * Save all events.
     */
    fun save(events: List<Event>): Mono<Void>

    /**
     * Read all events for the given id.
     * @param id - the id of the aggregate
     * @param sequence - starting of the sequence for the events, it is an optional. When is not specified, all events
     * are read frf the given id.
     */
    fun read(id: String, sequence: Long? = null): Flux<Event>

    /**
     * Get last sequence number of the event for the given id.
     * Sequence number is an index of the event for the given id.
     */
    fun getSequenceNumber(query: SequenceNumberQuery): Mono<Long>
}