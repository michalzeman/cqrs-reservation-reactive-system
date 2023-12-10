package com.mz.ddd.common.persistence.eventsourcing.internal

import com.mz.ddd.common.api.domain.*
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

typealias PublishDocument<S> = (S) -> Mono<Void>
typealias PublishChanged<E> = (E) -> Mono<Void>

internal class AggregateManagerImpl<A : Aggregate, C : DomainCommand, E : DomainEvent, S : Document<E>>(
    private val aggregateRepository: AggregateRepository<A, C, E>,
    private val aggregateMapper: (CommandEffect<A, E>) -> S,
    private val publishChanged: PublishChanged<E>? = null,
    private val publishDocument: PublishDocument<S>? = null
) : AggregateManager<A, C, E, S> {

    override fun execute(command: C, id: Id): Mono<S> =
        aggregateRepository.execute(id, command)
            .flatMap { effect ->
                val document: S = aggregateMapper(effect)
                publishChanged?.let { publisher ->
                    Flux.fromIterable(effect.events)
                        .flatMap { publisher.invoke(it) }
                        .collectList().thenReturn(document)
                } ?: document.toMono()
            }
            .flatMap { publishDocument?.invoke(it)?.thenReturn(it) ?: it.toMono() }


    override fun executeAndReturnEvents(command: C, id: Id): Mono<List<E>> =
        aggregateRepository.execute(command = command, id = id)
            .flatMap { effect ->
                publishDocument?.let { publisher ->
                    publisher(aggregateMapper(effect))
                }?.thenReturn(effect.events) ?: effect.events.toMono()
            }
            .flatMap { events ->
                publishChanged?.let { publisher ->
                    Flux.fromIterable(events)
                        .flatMap { publisher.invoke(it) }
                        .collectList().thenReturn(events)
                } ?: events.toMono()
            }

    override fun findById(id: Id): Mono<S> = aggregateRepository.find(id)
        .map { CommandEffect<A, E>(it) }
        .map(aggregateMapper)
}