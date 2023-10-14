package com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

data class SequenceNumberQuery(val aggregateId: String, val tag: String)

/**
 * Event journal data.
 */
data class EventJournal(
    val aggregateId: String,
    val sequenceNumber: Long,
    val createdAt: Instant,
    val tag: String,
    val payload: ByteArray,
    val payloadType: String
)

data class SnapshotAggregate(
    val aggregateId: String,
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
    fun save(eventJournals: List<EventJournal>): Mono<Void>

    /**
     * Read all events for the given id.
     * @param aggregateId - the id of the aggregate
     * @param sequence - starting of the sequence for the events, it is an optional. When is not specified, all events
     * are read frf the given id.
     */
    fun read(aggregateId: String, sequence: Long? = null): Flux<EventJournal>

    /**
     * Get last sequence number of the event for the given id.
     * Sequence number is an index of the event for the given id.
     */
    fun getSequenceNumber(query: SequenceNumberQuery): Mono<Long>
}