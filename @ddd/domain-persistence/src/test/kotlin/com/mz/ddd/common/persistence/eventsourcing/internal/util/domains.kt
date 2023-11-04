package com.mz.ddd.common.persistence.eventsourcing.internal.util

import com.mz.ddd.common.api.domain.*
import com.mz.ddd.common.persistence.eventsourcing.event.DomainTag
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val testTag = DomainTag(TestAggregate::class.java.name)

sealed class TestAggregate : Aggregate() {
    abstract val aggregateId: Id
}

data class EmptyTestAggregate(override val aggregateId: Id) : TestAggregate() {

}

data class ExistingTestAggregate(override val aggregateId: Id, val value: ValueVo) : TestAggregate() {

}

@Serializable
data class ValueVo(val value: String)

data class TestDocument(
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val docId: Id = Id(uuid()),
    val value: String,
    override val events: Set<TestEvent> = setOf()
) : Document<TestEvent>

@Serializable
sealed class TestEvent : DomainEvent {
    abstract val aggregateId: Id
}

@Serializable
@SerialName("TestAggregateCreated")
data class TestAggregateCreated(
    override val aggregateId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid()),
    val value: ValueVo
) : TestEvent()

@Serializable
@SerialName("TestValueUpdated")
data class TestValueUpdated(
    override val aggregateId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid()),
    val value: ValueVo
) : TestEvent()

sealed class TestCommand : DomainCommand

data class CreateTestAggregate(
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid()),
    val value: ValueParam<*>
) : TestCommand()

data class UpdateTestValue(
    val aggregateId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = Id(uuid()),
    val value: ValueParam<*>
) : TestCommand()

sealed interface ValueParam<V> {
    val value: V
}

data class IntValueParam(override val value: Int) : ValueParam<Int>
data class StringValueParam(override val value: String) : ValueParam<String>
data class BooleanValueParam(override val value: Boolean) : ValueParam<Boolean>
