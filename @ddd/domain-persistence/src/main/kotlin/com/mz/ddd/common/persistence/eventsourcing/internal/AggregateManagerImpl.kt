package com.mz.ddd.common.persistence.eventsourcing.internal

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import reactor.core.publisher.Mono

typealias PublishDocument<S> = (S) -> Unit
typealias PublishChanged<E> = (E) -> Unit

internal class AggregateManagerImpl<A : Aggregate, C : DomainCommand, E : DomainEvent, S>(
    private val aggregateRepository: AggregateRepository<A, C, E>,
    private val aggregateMapper: (CommandEffect<A, E>) -> S,
    private val publishChanged: PublishChanged<E>? = null,
    private val publishDocument: PublishDocument<S>? = null
) : AggregateManager<A, C, E, S> {

    override fun execute(command: C, id: Id): Mono<S> =
        aggregateRepository.execute(id, command)
            .map { effect ->
                publishChanged?.let { publisher -> effect.events.forEach(publisher::invoke) }
                aggregateMapper(effect)
            }
            .doOnNext { publishDocument?.invoke(it) }


    override fun executeAndReturnEvents(command: C, id: Id): Mono<List<E>> =
        aggregateRepository.execute(command = command, id = id)
            .map { effect ->
                publishDocument?.let { publisher ->
                    aggregateMapper(effect).also { state -> publisher.invoke(state) }
                }
                effect.events
            }
            .doOnNext { events -> publishChanged?.let { events.forEach(it::invoke) } }

    override fun findById(id: Id): Mono<S> = aggregateRepository.find(id)
        .map { CommandEffect<A, E>(it) }
        .map(aggregateMapper)
}