package com.mz.ddd.common.persistence.eventsourcing

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepositoryImpl
import com.mz.ddd.common.persistence.eventsourcing.event.DomainTag
import com.mz.ddd.common.persistence.eventsourcing.event.EventRepositoryImpl
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.EventStorageAdapter
import com.mz.ddd.common.persistence.eventsourcing.internal.DomainManagerImpl
import com.mz.ddd.common.persistence.eventsourcing.internal.PublishChanged
import com.mz.ddd.common.persistence.eventsourcing.internal.PublishDocument
import com.mz.ddd.common.persistence.eventsourcing.locking.internal.LockManagerImpl
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockStorageAdapter


/**
 * Data storage adapters configuration.
 * @param eventStorageAdapter   - Data storage for persisting of domain events.
 * @param eventSerDesAdapter      - Domain event serializer and deserializer domain event payload to be capable
 *                                serialise and domain event is storing into the event storage and deserialize
 *                                domain event when is loading from the event storage.
 *
 * @param lockStorageAdapter    - Lock storage adapter necessary for the lock and unlock of the Aggregate.
 */
data class DataStorageAdaptersConfig<E : DomainEvent>(
    val eventStorageAdapter: EventStorageAdapter,
    val eventSerDesAdapter: EventSerDesAdapter<E>,
    val lockStorageAdapter: LockStorageAdapter
)

object DomainPersistenceFactory {

    fun <A, C : DomainCommand, E : DomainEvent> buildAggregateRepository(
        domainTag: DomainTag,
        aggregateFactory: (Id) -> A,
        aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
        aggregateEventHandler: AggregateEventHandler<A, E>,
        dataStorageAdaptersConfig: com.mz.ddd.common.persistence.eventsourcing.DataStorageAdaptersConfig<E>
    ): AggregateRepository<A, C, E> =
        AggregateRepositoryImpl(
            aggregateFactory,
            com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessorImpl(
                aggregateCommandHandler,
                aggregateEventHandler
            ),
            EventRepositoryImpl(
                domainTag,
                dataStorageAdaptersConfig.eventStorageAdapter,
                dataStorageAdaptersConfig.eventSerDesAdapter
            ),
            LockManagerImpl(dataStorageAdaptersConfig.lockStorageAdapter)
        )

    fun <A, C : DomainCommand, E : DomainEvent, S> buildDomainManager(
        aggregateRepository: AggregateRepository<A, C, E>,
        aggregateMapper: (A) -> S,
        publishChanged: PublishChanged<E>? = null,
        publishDocument: PublishDocument<S>? = null
    ): com.mz.ddd.common.persistence.eventsourcing.DomainManager<A, C, E, S> =
        DomainManagerImpl(aggregateRepository, aggregateMapper, publishChanged, publishDocument)

}