package com.mz.common.persistence.eventsourcing

import com.mz.common.persistence.eventsourcing.event.EventRepository
import com.mz.common.persistence.eventsourcing.event.EventStorageAdapter
import com.mz.common.persistence.eventsourcing.internal.event.EventRepositoryImpl
import com.mz.reservation.common.api.domain.DomainEvent

object EventSourcingPersistenceFactory {

    fun <E : DomainEvent> build(eventStorageAdapter: EventStorageAdapter): EventRepository<E> =
        EventRepositoryImpl(eventStorageAdapter)

}