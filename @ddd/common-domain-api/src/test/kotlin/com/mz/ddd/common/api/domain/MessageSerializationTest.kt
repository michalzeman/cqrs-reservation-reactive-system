package com.mz.ddd.common.api.domain

import kotlinx.datetime.Instant
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

class MessageSerializationTest {

    @Test
    fun `serialization of the list of domain events of several versions`() {
        val testAggregateCreated = TestAggregateCreated("1", value = ValueVo("1"))
        val testValueUpdated = TestValueUpdated("1", value = ValueVo("2"))

        val domainEvents: List<TestEvent> = listOf(
            testAggregateCreated,
            testValueUpdated
        )

        val jsons = domainEvents.map { serToJsonString(it) }

        val decodedDomainEvents = jsons.map { desJson<TestEvent>(it) }

        assertThat(decodedDomainEvents).containsExactlyInAnyOrder(testAggregateCreated, testValueUpdated)
    }

    private inline fun <reified T : DomainEvent> serToJsonString(value: T) = Json.encodeToString(value)

    private inline fun <reified T : DomainEvent> desJson(value: String): T {
        return Json.decodeFromString<T>(value)
    }

}