package com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventJournal
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@Serializable
data class ValueVo(val value: String)

@Serializable
sealed class TestEvent : DomainEvent {
    abstract val aggregateId: String
}

@Serializable
@SerialName("TestAggregateCreated")
data class TestAggregateCreated(
    override val aggregateId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val eventId: String = uuid(),
    val value: ValueVo
) : TestEvent()

@Serializable
@SerialName("TestValueUpdated")
data class TestValueUpdated(
    override val aggregateId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val eventId: String = uuid(),
    val value: ValueVo
) : TestEvent()

class JsonEventSerDesAdapterTest {

    val subject = EventSerDesAdapter<TestEvent>(
        encode = { event -> serToJsonString(event).encodeToByteArray() },
        decode = { json -> desJson(json) }
    )

    @Test
    fun `serialization domain event to ByteArray`() {
        val aggregateCreated = TestAggregateCreated("1", value = ValueVo("1"))
        val serialized = subject.serialize(aggregateCreated)

        val desEvent = desJson<TestEvent>(serialized.decodeToString())

        assertThat(desEvent is TestAggregateCreated).isTrue()
        assertThat(desEvent).isEqualTo(aggregateCreated)
    }

    @Test
    fun `deserialization of the Event instance`() {
        val testAggregateCreated = TestAggregateCreated("1", value = ValueVo("1"))
        val payload = subject.serialize(testAggregateCreated)

        val eventJournal = EventJournal(
            aggregateId = "1",
            sequenceNumber = 1,
            createdAt = instantNow().toJavaInstant(),
            tag = "TestAggregate",
            payloadType = "ApplicationContent/Json",
            payload = payload
        )

        val desEvent = subject.deserialize(eventJournal)

        assertThat(desEvent is TestAggregateCreated).isTrue()
        assertThat(desEvent).isEqualTo(testAggregateCreated)
    }

    private inline fun <reified T : DomainEvent> serToJsonString(value: T) = Json.encodeToString(value)

    private inline fun <reified T : DomainEvent> desJson(value: String): T {
        return Json.decodeFromString<T>(value)
    }
}