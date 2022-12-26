package com.mz.common.persistence.eventsourcing.internal.util

import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.uuid
import java.time.Instant

sealed interface TestAggregate {
    val aggregateId: String
}

data class EmptyTestAggregate(override val aggregateId: String) : TestAggregate {

}

data class ExistingTestAggregate(override val aggregateId: String, val value: ValueVo) : TestAggregate {

}

data class ValueVo(val value: String)

sealed interface TestEvent : DomainEvent {
    val aggregateId: String
}

data class TestAggregateCreated(
    override val aggregateId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val eventId: String = uuid(),
    val value: ValueVo
) : TestEvent

data class TestValueUpdated(
    override val aggregateId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val eventId: String = uuid(),
    val value: ValueVo
) : TestEvent

sealed interface TestCommand : DomainCommand

data class CreateTestAggregate(
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val commandId: String = uuid(),
    val value: ValueParam<*>
) : TestCommand

data class UpdateTestValue(
    val aggregateId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val commandId: String = uuid(),
    val value: ValueParam<*>
) : TestCommand

sealed interface ValueParam<V> {
    val value: V
}

data class IntValueParam(override val value: Int) : ValueParam<Int>
data class StringValueParam(override val value: String) : ValueParam<String>
data class BooleanValueParam(override val value: Boolean) : ValueParam<Boolean>
