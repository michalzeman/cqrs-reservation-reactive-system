package com.mz.ddd.common.api.domain.command

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.util.Try

interface AggregateCommandHandler<A, C : DomainCommand, E : DomainEvent> {

    fun execute(aggregate: A, command: C): Try<List<E>>

}