package com.mz.ddd.common.persistence.eventsourcing.internal

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import reactor.core.publisher.Mono

typealias PublishDocument<S> = (S) -> Unit
typealias PublishChanged<E> = (E) -> Unit

internal class AggregateManagerImpl<A, C : DomainCommand, E : DomainEvent, S>(
    private val aggregateRepository: AggregateRepository<A, C, E>,
    private val aggregateMapper: (A) -> S,
    private val publishChanged: PublishChanged<E>? = null,
    private val publishDocument: PublishDocument<S>? = null
) : AggregateManager<A, C, E, S> {

    override fun execute(command: C, id: Id): Mono<S> =
        aggregateRepository.execute(id, command)
            .map { effect ->
                effect.events.forEach { event -> publishChanged?.invoke(event) }
                aggregateMapper(effect.aggregate)
            }
            .doOnNext { publishDocument?.invoke(it) }


    override fun executeAndReturnEvents(command: C, id: Id): Mono<List<E>> =
        aggregateRepository.execute(command = command, id = id)
            .doOnNext { effect ->
                effect.events.forEach { event -> publishChanged?.invoke(event) }
                aggregateMapper(effect.aggregate).also { state -> publishDocument?.invoke(state) }
            }
            .map { it.events }

    override fun findById(id: Id): Mono<S> = aggregateRepository.find(id).map(aggregateMapper)
}