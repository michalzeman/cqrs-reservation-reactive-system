package com.mz.common.persistence.eventsourcing.event

import com.mz.common.persistence.eventsourcing.event.storage.adapter.Event
import com.mz.reservation.common.api.domain.DomainEvent

interface EventSerdAdapter<E : DomainEvent> {
    fun serialize(event: E): ByteArray

    fun deserialize(event: Event): E
}