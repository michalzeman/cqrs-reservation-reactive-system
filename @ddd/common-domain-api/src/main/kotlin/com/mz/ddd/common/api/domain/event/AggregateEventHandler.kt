package com.mz.ddd.common.api.domain.event

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainEvent

interface AggregateEventHandler<A : Aggregate, E : DomainEvent> {
    fun apply(aggregate: A, event: E): A
}