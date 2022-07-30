package com.mz.common.persistence.eventsourcing.aggregate.internal

import com.mz.common.persistence.eventsourcing.aggregate.AggregateProcessor
import com.mz.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.common.persistence.eventsourcing.aggregate.AggregateRepositoryBuilder
import com.mz.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.common.persistence.eventsourcing.aggregate.locking.AcquireLock
import com.mz.common.persistence.eventsourcing.aggregate.locking.LockManager
import com.mz.common.persistence.eventsourcing.aggregate.locking.LockReleased
import com.mz.common.persistence.eventsourcing.aggregate.locking.ReleaseLock
import com.mz.common.persistence.eventsourcing.event.Event
import com.mz.common.persistence.eventsourcing.event.EventRepository
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.util.Failure
import com.mz.reservation.common.api.util.Success
import reactor.core.publisher.Mono


internal class AggregateRepositoryImpl<A, C : DomainCommand, E : DomainEvent, S>(
    aggregateRepositoryBuilder: AggregateRepositoryBuilder<A, C, E, S>,
    private val lockManager: LockManager,
    private val eventRepository: EventRepository<E>
) : AggregateRepository<A, C, E, S> {

    private val aggregateProcessor: AggregateProcessor<A, C, E> = AggregateProcessor.create(
        aggregateRepositoryBuilder.aggregateCommandHandler,
        aggregateRepositoryBuilder.aggregateEventHandler
    )

    private val aggregateFactory: (Id) -> A = aggregateRepositoryBuilder.aggregateFactory

    private val domainTag = aggregateRepositoryBuilder.domainTag

    override fun execute(id: Id, command: C): Mono<CommandEffect<A, E>> {

        return lockManager.acquireLock(AcquireLock({ id.value }, { command.toString() }))
            .then(
                executeCommandViaCommandProcessor(
                    id,
                    command,
                    lockManager.releaseLock(ReleaseLock { id.value })
                )
            )
    }

    private fun executeCommandViaCommandProcessor(
        id: Id,
        command: C,
        releaseLock: Mono<LockReleased>
    ): Mono<CommandEffect<A, E>> {
        return when (val effect = aggregateProcessor.execute(aggregateFactory(id), command)) {
            is Success -> persistAllEvents(id, effect.result.events)
                .and(releaseLock)
                .thenReturn(effect.result)

            is Failure -> releaseLock.then(Mono.error(effect.exc))
        }
    }

    private fun persistAllEvents(id: Id, events: List<E>): Mono<Void> {
        return eventRepository.persistAll(id, events)
    }

    private fun getAggregate(id: Id): Mono<A> {
//        Mono.fromCallable { aggregateFactory(id) }
//            .flatMap { agr -> eventRepository.load(id, domainTag).toStream() }

//        eventRepository.load(id, domainTag)
//            .map(this::mapPayloadToDomainEvent)
//            .filter { it != null }
//            .map { it!! }
//            .toStream().reduce(aggregateFactory(id), BiFunction { t, u ->  })

        return Mono.empty()
    }

    private fun mapPayloadToDomainEvent(event: Event): E? {
        return null
    }
}