package com.mz.ddd.common.api.domain.command

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent

interface AggregateCommandHandler<A : Aggregate, C : DomainCommand, E : DomainEvent> {

    fun execute(aggregate: A, command: C): Result<List<E>>

}