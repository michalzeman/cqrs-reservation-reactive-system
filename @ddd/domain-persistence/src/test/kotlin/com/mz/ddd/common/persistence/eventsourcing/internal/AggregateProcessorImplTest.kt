package com.mz.ddd.common.persistence.eventsourcing.internal

import com.mz.ddd.common.api.domain.uuid
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessor
import com.mz.ddd.common.persistence.eventsourcing.internal.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AggregateProcessorImplTest {

    private val commandHandler = TestCommandHandler()

    private val eventHandler = TestEventHandler()

    private val subject = AggregateProcessor(commandHandler, eventHandler)

    @Test
    internal fun execute() {
        val aggregateId = uuid()
        val emptyTestAggregate = EmptyTestAggregate(aggregateId)

        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)

        val resultCreated = subject.execute(emptyTestAggregate, createTestAggregate)

        assertThat(resultCreated).isInstanceOf(resultCreated.javaClass)

        val existingAggregate = resultCreated.getOrThrow().aggregate as ExistingTestAggregate
        assertThat(existingAggregate.aggregateId).isEqualTo(aggregateId)
        assertThat(existingAggregate.value.value).isEqualTo(stringInitValue.value)

        val events = resultCreated.getOrThrow().events
        assertThat(events.all { it is TestAggregateCreated }).isTrue

        val updatedIntResult =
            subject.execute(existingAggregate, UpdateTestValue(value = IntValueParam(2), aggregateId = aggregateId))
        assertThat(updatedIntResult.isSuccess).isTrue
        assertThat((updatedIntResult.getOrThrow().aggregate as ExistingTestAggregate).value.value).isEqualTo("${stringInitValue.value} 2")
    }

    @Test
    internal fun executeFailure() {
        val aggregateId = uuid()
        val emptyTestAggregate = EmptyTestAggregate(aggregateId)

        val stringInitValue = StringValueParam("Hello there\n")
        val cmd = UpdateTestValue(aggregateId = aggregateId, value = stringInitValue)

        val result = subject.execute(emptyTestAggregate, cmd)
        assertThat(result.isFailure).isTrue
    }

}