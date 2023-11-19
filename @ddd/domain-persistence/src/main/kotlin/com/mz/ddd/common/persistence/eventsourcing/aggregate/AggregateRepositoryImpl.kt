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
import reactor.kotlin.core.publisher.switchIfEmpty


internal class AggregateRepositoryImpl<A : Aggregate, C : DomainCommand, E : DomainEvent>(
    private val aggregateFactory: (Id) -> Mono<A>,
    private val aggregateProcessor: AggregateProcessor<A, C, E>,
    private val eventRepository: EventRepository<E, A>,
    private val lockManager: LockManager,
    private val serDesAdapter: EventSerDesAdapter<E, A>
) : AggregateRepository<A, C, E> {

    override fun execute(id: Id, command: C): Mono<CommandEffect<A, E>> =
        lockManager.acquireLock(AcquireLock({ id.value }, { command.toString() }))
            .then(
                executeCommandViaCommandProcessor(
                    id,
                    command,
                    lockManager.releaseLock(ReleaseLock { id.value })
                )
            )

    override fun find(id: Id): Mono<A> = getAggregate(id)

    private fun executeCommandViaCommandProcessor(
        id: Id,
        command: C,
        releaseLock: Mono<LockReleased>
    ): Mono<CommandEffect<A, E>> =
        getAggregate(id)
            .flatMap { aggregate ->
                aggregateProcessor.execute(aggregate, command)
                    .fold(
                        onSuccess = { success ->
                            eventRepository.persistAll(success)
                                .and(releaseLock)
                                .thenReturn(success)
                        },
                        onFailure = { exc -> releaseLock.then(Mono.error(exc)) }
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
                aggregateFactory(id)
                    .flatMap {
                        eventRepository
                            .read(id)
                            .reduceWith(
                                { it },
                                { aggregate, event -> aggregateProcessor.applyEvents(aggregate, listOf(event)) })
                    }
            }
    }
}