package com.mz.ddd.common.persistence.eventsourcing

import com.mz.ddd.common.api.domain.*
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventStorageAdapter
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessorImpl
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepositoryImpl
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.EventRepositoryImpl
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.internal.AggregateManagerImpl
import com.mz.ddd.common.persistence.eventsourcing.internal.PublishChanged
import com.mz.ddd.common.persistence.eventsourcing.internal.PublishDocument
import com.mz.ddd.common.persistence.eventsourcing.locking.internal.LockManagerImpl
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockStorageAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import

@Import(DomainPersistenceConfiguration::class)
abstract class AbstractEventSourcingConfiguration<A : Aggregate, C : DomainCommand, E : DomainEvent, S : Document<E>> {

    @Autowired
    lateinit var eventStorageAdapter: EventStorageAdapter

    @Autowired
    lateinit var lockStorageAdapter: LockStorageAdapter

    @Autowired
    lateinit var domainPersistenceProperties: DomainPersistenceProperties

    abstract fun aggregateRepository(): AggregateRepository<A, C, E>

    abstract fun aggregateManager(
        aggregateRepository: AggregateRepository<A, C, E>,
        aggregateMapper: (CommandEffect<A, E>) -> S
    ): AggregateManager<A, C, E, S>

    abstract fun eventSerDesAdapter(): EventSerDesAdapter<E, A>

    abstract fun domainTag(): DomainTag

    protected fun buildAggregateRepository(
        aggregateFactory: (Id) -> A,
        aggregateCommandHandler: AggregateCommandHandler<A, C, E>,
        aggregateEventHandler: AggregateEventHandler<A, E>
    ): AggregateRepository<A, C, E> =
        AggregateRepositoryImpl(
            aggregateFactory,
            AggregateProcessorImpl(
                aggregateCommandHandler,
                aggregateEventHandler
            ),
            EventRepositoryImpl(
                domainTag(),
                eventStorageAdapter,
                eventSerDesAdapter(),
                domainPersistenceProperties
            ),
            LockManagerImpl(lockStorageAdapter),
            eventSerDesAdapter()
        )

    /**
     * Build domain manager.
     * @param aggregateRepository - Aggregate repository.
     * @param aggregateMapper - Aggregate mapper.
     * @param publishChanged - Publish changed document.
     *
     * A - Aggregate type
     * C - Command type
     * E - Event type
     * S - State type
     */
    protected fun buildAggregateManager(
        aggregateRepository: AggregateRepository<A, C, E>,
        aggregateMapper: (CommandEffect<A, E>) -> S,
        publishChanged: PublishChanged<E>? = null,
        publishDocument: PublishDocument<S>? = null
    ): AggregateManager<A, C, E, S> =
        AggregateManagerImpl(aggregateRepository, aggregateMapper, publishChanged, publishDocument)

}
