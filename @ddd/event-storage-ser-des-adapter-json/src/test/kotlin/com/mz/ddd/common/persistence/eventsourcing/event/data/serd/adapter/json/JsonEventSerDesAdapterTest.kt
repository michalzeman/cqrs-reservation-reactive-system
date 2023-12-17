package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Serializable
data class TestAggregate(override val aggregateId: Id, val createdAt: Instant = instantNow()) : Aggregate()

@JvmInline
@Serializable
value class ValueVo(val value: String)

@Serializable
sealed class TestEvent : DomainEvent {
    abstract val aggregateId: Id
}

@Serializable
@SerialName("TestAggregateCreated")
data class TestAggregateCreated(
    override val aggregateId: Id,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId(),
    val value: ValueVo
) : TestEvent()

@Serializable
@SerialName("TestValueUpdated")
data class TestValueUpdated(
    override val aggregateId: Id,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId(),
    val value: ValueVo
) : TestEvent()

class JsonEventSerDesAdapterTest {

    val subject = JsonEventSerDesAdapter<TestEvent, TestAggregate>(
        encodeEvent = { event -> serToJsonString(event) },
        decodeEvent = { json -> desJson(json) },
        encodeAggregate = { aggregate -> serToJsonString(aggregate) },
        decodeAggregate = { json -> desJson(json) }
    )

    @Test
    fun `serialization domain event to ByteArray`() {
        val aggregateCreated = TestAggregateCreated(Id("1"), value = ValueVo("1"))
        val serialized = subject.serialize(aggregateCreated)

        val desEvent = desJson<TestEvent>(serialized.decodeToString())

        assertThat(desEvent is TestAggregateCreated).isTrue()
        assertThat(desEvent).isEqualTo(aggregateCreated)
    }

    @Test
    fun `deserialization of the Event instance`() {
        val testAggregateCreated = TestAggregateCreated(Id("1"), value = ValueVo("1"))
        val payload = subject.serialize(testAggregateCreated)

        val eventJournal = EventJournal(
            aggregateId = "1",
            sequenceNumber = 1,
            createdAt = instantNow().toJavaInstant(),
            tag = "TestAggregate",
            payloadType = "ApplicationContent/json",
            payload = payload
        )

        val desEvent = subject.deserialize(eventJournal)

        assertThat(desEvent is TestAggregateCreated).isTrue()
        assertThat(desEvent).isEqualTo(testAggregateCreated)
    }

    @Test
    fun `serialization aggregate to ByteArray`() {
        val aggregate = TestAggregate(Id("1"))
        val serialized = subject.serialize(aggregate)

        val desAggregate = desJson<TestAggregate>(serialized.decodeToString())

        Assertions.assertEquals(desAggregate, aggregate)
    }

    @Test
    fun `deserialization aggregate from ByteArray`() {
        val aggregate = TestAggregate(Id("1"))
        val payload = subject.serialize(aggregate)

        val snapshot = Snapshot(
            aggregateId = aggregate.aggregateId.value,
            sequenceNumber = 1L,
            createdAt = instantNow().toJavaInstant(),
            tag = "tag",
            payloadType = "ApplicationContent/json",
            payload = payload
        )

        val desAggregate = subject.deserialize(snapshot)

        Assertions.assertEquals(desAggregate, aggregate)
    }
}