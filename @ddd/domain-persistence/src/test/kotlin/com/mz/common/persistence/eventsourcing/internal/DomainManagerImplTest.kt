package com.mz.common.persistence.eventsourcing.internal

import com.mz.common.persistence.eventsourcing.DomainManager
import com.mz.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.common.persistence.eventsourcing.internal.util.*
import com.mz.reservation.common.api.domain.Id
import com.mz.reservation.common.api.domain.uuid
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
internal class DomainManagerImplTest {

    @Test
    fun execute() {
        val aggregateId = uuid()
        val emptyTestAggregate = EmptyTestAggregate(aggregateId)

        val commandEffect = CommandEffect<TestAggregate, TestEvent>(emptyTestAggregate, listOf())

        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = Id(aggregateId)

        val aggregateRepository = mock<AggregateRepository<TestAggregate, TestCommand, TestEvent>> {
            on { execute(id, createTestAggregate) } doReturn Mono.just(commandEffect)
        }

        val subject = subject(aggregateRepository) { _ -> "String" }

        val result = subject.execute(createTestAggregate, aggregateId)

        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }


    @Test
    fun executeAndReturnEvents() {
        val aggregateId = uuid()
        val emptyTestAggregate = EmptyTestAggregate(aggregateId)
        val stringInitValue = StringValueParam("Hello there\n")

        val commandEffect = CommandEffect<TestAggregate, TestEvent>(
            emptyTestAggregate,
            listOf(TestAggregateCreated(aggregateId = aggregateId, value = ValueVo(stringInitValue.value)))
        )

        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = Id(aggregateId)

        val aggregateRepository = mock<AggregateRepository<TestAggregate, TestCommand, TestEvent>> {
            on { execute(id, createTestAggregate) } doReturn Mono.just(commandEffect)
        }

        val subject = subject(aggregateRepository) { _ -> "String" }

        val result = subject.executeAndReturnEvents(createTestAggregate, aggregateId)

        StepVerifier.create(result)
            .expectNextMatches { events -> events.size == 1 && events[0] is TestAggregateCreated }
            .verifyComplete()
    }

    @Test
    fun findById() {
        val aggregateId = uuid()
        val id = Id(aggregateId)
        val value = ValueVo("Existing Aggregate")
        val aggregate = ExistingTestAggregate(aggregateId, value)

        val aggregateRepository = mock<AggregateRepository<TestAggregate, TestCommand, TestEvent>> {
            on { find(id) } doReturn Mono.just(aggregate)
        }

        val aggregateMapper = { agg: TestAggregate ->
            when (agg) {
                is EmptyTestAggregate -> "Empty aggregate ${aggregateId}"
                is ExistingTestAggregate -> agg.value.value
            }
        }

        val subject = subject(aggregateRepository, aggregateMapper)
        val result = subject.findById(aggregateId)
        StepVerifier.create(result)
            .expectNext(value.value)
            .verifyComplete()
    }

    private fun subject(
        aggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>,
        aggregateMapper: (TestAggregate) -> String
    ): DomainManager<TestAggregate, TestCommand, TestEvent, String> {
        return DomainManagerImpl(aggregateRepository, aggregateMapper = aggregateMapper)
    }
}