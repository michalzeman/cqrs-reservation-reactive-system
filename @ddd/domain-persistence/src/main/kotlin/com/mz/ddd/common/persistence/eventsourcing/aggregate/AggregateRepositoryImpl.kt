package com.mz.ddd.common.persistence.eventsourcing.aggregate

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.event.EventRepository
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.LockManager
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono


internal class AggregateRepositoryImpl<A : Aggregate, C : DomainCommand, E : DomainEvent>(
    private val aggregateFactory: (Id) -> A,
    private val aggregateProcessor: AggregateProcessor<A, C, E>,
    private val eventRepository: EventRepository<E, A>,
    private val lockManager: LockManager,
    private val serDesAdapter: EventSerDesAdapter<E, A>
) : AggregateRepository<A, C, E> {

    override fun execute(
        id: Id,
        command: C,
        exclusivePreExecute: (() -> Mono<Boolean>)?
    ): Mono<CommandEffect<A, E>> {
        val passed = exclusivePreExecute?.invoke()?.publishOn(Schedulers.boundedElastic()) ?: true.toMono()
        return lockManager.acquireLock(AcquireLock({ id.value }, { command.toString() }))
            .flatMap { passed }
            .flatMap { canExecute ->
                if (canExecute) {
                    executeCommandViaCommandProcessor(
                        id,
                        command
                    )
                } else {
                    Mono.error(IllegalStateException("Command $command can not be executed, exclusive precondition failed"))
                }
            }
            .flatMap { releaseLock(id).thenReturn(it) }
            .onErrorResume { error -> releaseLock(id).then(Mono.error(error)) }
    }

    override fun find(id: Id): Mono<A> = getAggregate(id)

    private fun executeCommandViaCommandProcessor(
        id: Id,
        command: C
    ): Mono<CommandEffect<A, E>> =
        getAggregate(id)
            .flatMap { aggregate ->
                aggregateProcessor.execute(aggregate, command)
                    .fold(
                        onSuccess = { success ->
                            eventRepository.persistAll(success)
                                .thenReturn(success)
                        },
                        onFailure = { exc -> Mono.error(exc) }
                    )
            }


    private fun getAggregate(id: Id): Mono<A> {
        return eventRepository.readSnapshot(id)
            .map { snapshot ->
                val aggregate = serDesAdapter.deserialize(snapshot)
                val events = snapshot.eventJournals.map(serDesAdapter::deserialize)
                aggregateProcessor.applyEvents(aggregate, events)
            }
            .switchIfEmpty {
                eventRepository
                    .read(id)
                    .reduceWith(
                        { aggregateFactory(id) },
                        { aggregate, event -> aggregateProcessor.applyEvents(aggregate, listOf(event)) })
            }
    }

    private fun releaseLock(id: Id): Mono<LockReleased> {
        return lockManager.releaseLock(ReleaseLock { id.value })
    }
}