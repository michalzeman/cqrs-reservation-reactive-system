package com.mz.ddd.common.persistence.eventsourcing.internal

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import reactor.core.publisher.Mono

typealias PublishDocument<S> = (S) -> Unit
typealias PublishChanged<E> = (E) -> Unit

internal class DomainManagerImpl<A, C : DomainCommand, E : DomainEvent, S>(
    private val aggregateRepository: AggregateRepository<A, C, E>,
    private val aggregateMapper: (A) -> S,
    private val publishChanged: PublishChanged<E>? = null,
    private val publishDocument: PublishDocument<S>? = null
) : com.mz.ddd.common.persistence.eventsourcing.DomainManager<A, C, E, S> {

    override fun execute(command: C, id: String): Mono<S> =
        aggregateRepository.execute(Id(id), command)
            .map { effect ->
                effect.events.forEach { event -> publishChanged?.invoke(event) }
                aggregateMapper(effect.aggregate)
            }
            .doOnNext { publishDocument?.invoke(it) }


    override fun executeAndReturnEvents(command: C, id: String): Mono<List<E>> =
        aggregateRepository.execute(command = command, id = Id(id))
            .doOnNext { effect ->
                effect.events.forEach { event -> publishChanged?.invoke(event) }
                aggregateMapper(effect.aggregate).also { state -> publishDocument?.invoke(state) }
            }
            .map { it.events }

    override fun findById(id: String): Mono<S> = aggregateRepository.find(Id(id)).map(aggregateMapper)
}