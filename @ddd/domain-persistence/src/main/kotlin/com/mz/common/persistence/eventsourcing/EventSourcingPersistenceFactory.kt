package com.mz.common.persistence.eventsourcing

import com.mz.common.persistence.eventsourcing.event.EventRepository
import com.mz.common.persistence.eventsourcing.event.EventRepositoryImpl
import com.mz.common.persistence.eventsourcing.event.EventSerDeSerAdapter
import com.mz.common.persistence.eventsourcing.event.EventStorageAdapter
import com.mz.reservation.common.api.domain.DomainEvent

object EventSourcingPersistenceFactory {

    fun <E : DomainEvent> build(
        eventStorageAdapter: EventStorageAdapter,
        eventSerDeSerAdapter: EventSerDeSerAdapter<E>
    ): EventRepository<E> =
        EventRepositoryImpl(eventStorageAdapter, eventSerDeSerAdapter)

}