package com.mz.ddd.common.persistence.eventsourcing.event

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.persistence.eventsourcing.event.storage.adapter.Event

interface EventSerdAdapter<E : DomainEvent> {
    fun serialize(event: E): ByteArray

    fun deserialize(event: Event): E
}