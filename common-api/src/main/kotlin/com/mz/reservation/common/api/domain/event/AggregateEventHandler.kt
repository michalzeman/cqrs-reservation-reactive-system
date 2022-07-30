package com.mz.reservation.common.api.domain.event

import com.mz.reservation.common.api.domain.DomainEvent

interface AggregateEventHandler<A, E : DomainEvent> {
    fun apply(aggregate: A, event: E): A
}