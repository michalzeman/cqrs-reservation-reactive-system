package com.mz.common.persistence.eventsourcing

import com.mz.common.persistence.eventsourcing.event.EventRepository
import com.mz.common.persistence.eventsourcing.event.EventRepositoryImpl
import com.mz.common.persistence.eventsourcing.event.EventSerdAdapter
import com.mz.common.persistence.eventsourcing.event.Tag
import com.mz.common.persistence.eventsourcing.event.storage.adapter.EventStorageAdapter
import com.mz.reservation.common.api.domain.DomainEvent

object EventSourcingPersistenceFactory {

    fun <E : DomainEvent> build(
        tag: Tag,
        eventStorageAdapter: EventStorageAdapter,
        eventSerdAdapter: EventSerdAdapter<E>
    ): EventRepository<E> =
        EventRepositoryImpl(tag, eventStorageAdapter, eventSerdAdapter)

}