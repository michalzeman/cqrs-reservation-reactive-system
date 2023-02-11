package com.mz.common.persistence.eventsourcing.aggregate

import com.mz.common.persistence.eventsourcing.event.EventRepository
import com.mz.common.persistence.eventsourcing.locking.LockManager
import com.mz.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.util.Failure
import com.mz.reservation.common.api.util.Success
import reactor.core.publisher.Mono


internal class AggregateRepositoryImpl<A, C : DomainCommand, E : DomainEvent>(
    private val aggregateFactory: (Id) -> A,
    private val aggregateProcessor: AggregateProcessor<A, C, E>,
    private val eventRepository: EventRepository<E>,
    private val lockManager: LockManager,
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
                when (val effect = aggregateProcessor.execute(aggregate, command)) {
                    is Success -> eventRepository.persistAll(id, effect.result.events)
                        .and(releaseLock)
                        .thenReturn(effect.result)

                    is Failure -> releaseLock.then(Mono.error(effect.exc))
                }
            }


    private fun getAggregate(id: Id): Mono<A> =
        eventRepository
            .read(id)
            .reduceWith(
                { aggregateFactory(id) },
                { aggregate, event -> aggregateProcessor.applyEvents(aggregate, listOf(event)) }
            )
}