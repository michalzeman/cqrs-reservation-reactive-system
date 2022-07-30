package com.mz.common.persistence.eventsourcing.event

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DataSource {

    fun save(events: List<Event>): Mono<Void>

    fun read(persistenceId: String, tag: String): Flux<Event>

    fun getSequenceNumber(persistenceId: String): Mono<Long>
}