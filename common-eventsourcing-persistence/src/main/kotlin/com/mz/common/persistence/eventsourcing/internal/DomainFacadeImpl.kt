package com.mz.common.persistence.eventsourcing.internal

import com.mz.common.persistence.eventsourcing.DomainFacade
import com.mz.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import reactor.core.publisher.Mono

typealias PublishDocument<S> = (S) -> Unit
typealias PublishChanged<E> = (E) -> Unit

internal class DomainFacadeImpl<A, C : DomainCommand, E : DomainEvent, S>(
    private val aggregateRepository: AggregateRepository<A, C, E>,
    private val aggregateMapper: (A) -> S,
    private val publishChanged: PublishChanged<E>? = null,
    private val publishDocument: PublishDocument<S>? = null
) : DomainFacade<A, C, E, S> {

    override fun execute(command: C, id: String): Mono<S> {
        return aggregateRepository.execute(Id(id), command)
            .map { effect ->
                effect.events.forEach { event -> publishChanged?.invoke(event) }
                aggregateMapper(effect.aggregate)
            }
            .doOnNext { publishDocument?.invoke(it) }
    }

    override fun executeAndReturnEvents(command: C, id: String): Mono<List<E>> {
        return aggregateRepository.execute(command = command, id = Id(id))
            .doOnNext { effect ->
                effect.events.forEach { event -> publishChanged?.invoke(event) }
                aggregateMapper(effect.aggregate).also { state -> publishDocument?.invoke(state) }
            }
            .map { it.events }
    }

    override fun findById(id: String): Mono<S> = aggregateRepository.find(Id(id)).map(aggregateMapper)
}