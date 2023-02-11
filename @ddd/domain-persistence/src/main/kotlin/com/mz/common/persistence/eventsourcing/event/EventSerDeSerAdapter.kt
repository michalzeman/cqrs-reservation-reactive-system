package com.mz.common.persistence.eventsourcing.event

import com.mz.reservation.common.api.domain.DomainEvent

interface EventSerDeSerAdapter<E : DomainEvent> {
    fun serialize(event: E): ByteArray

    fun deserialize(event: Event): E
}