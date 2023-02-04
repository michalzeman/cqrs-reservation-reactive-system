package com.mz.common.persistence.eventsourcing.event

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
    fun getSequenceNumber(id: String): Mono<Long>
}