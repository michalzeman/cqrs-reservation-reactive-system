package com.mz.common.persistence.eventsourcing.aggregate

import com.mz.common.persistence.eventsourcing.locking.LockManager
import com.mz.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.util.Failure
import com.mz.reservation.common.api.util.Success
import com.mz.reservation.common.api.util.Try
import reactor.core.publisher.Mono


internal class AggregateRepositoryImpl<A, C : DomainCommand, E : DomainEvent>(
    private val aggregateFactory: (Id) -> A,
    private val executeCommand: (A, C) -> Try<CommandEffect<A, E>>,
    private val persistAllEvents: (Id, List<E>) -> Mono<Unit>,
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

    private fun executeCommandViaCommandProcessor(
        id: Id,
        command: C,
        releaseLock: Mono<LockReleased>
    ): Mono<CommandEffect<A, E>> =
        when (val effect = executeCommand(aggregateFactory(id), command)) {
            is Success -> persistAllEvents(id, effect.result.events)
                .and(releaseLock)
                .thenReturn(effect.result)

            is Failure -> releaseLock.then(Mono.error(effect.exc))
        }

    override fun find(id: Id): Mono<A> = getAggregate(id)

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
}