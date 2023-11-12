package com.mz.ddd.common.persistence.eventsourcing

import com.mz.ddd.common.api.domain.newId
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.internal.util.*
import com.mz.ddd.common.persistence.eventsourcing.wiring.TestDomainPersistenceConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest(classes = [TestDomainPersistenceConfiguration::class])
class DomainPersistenceWorkFlowTest {

    @Autowired
    lateinit var testAggregateManager: AggregateManager<TestAggregate, TestCommand, TestEvent, TestDocument>

    @Autowired
    lateinit var testAggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>

    @Test
    fun `should execute command for creation of aggregate, aggregate is created`() {
        val aggregateId = newId()
        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = aggregateId

        val aggregate = testAggregateManager.execute(createTestAggregate, aggregateId)

        StepVerifier.create(aggregate)
            .expectNextCount(1)
            .verifyComplete()

        val loadAggregate = testAggregateRepository.find(id)

        StepVerifier.create(loadAggregate)
            .assertNext {
                assertThat(it).isInstanceOf(ExistingTestAggregate::class.java)
                assertThat(it.aggregateId).isEqualTo(aggregateId)
            }
            .verifyComplete()
    }

    @Test
    fun `creation aggregate and update it, aggregate is updated`() {
        val aggregateId = newId()
        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = aggregateId

        val updatedValue = "Updated 1"
        val updateTestAggregate = UpdateTestValue(aggregateId = aggregateId, value = StringValueParam(updatedValue))

        val aggregate = testAggregateManager.execute(createTestAggregate, aggregateId)
            .then(testAggregateManager.execute(updateTestAggregate, aggregateId))

        StepVerifier.create(aggregate)
            .expectNextCount(1)
            .verifyComplete()

        val loadAggregate = testAggregateRepository.find(id)

        StepVerifier.create(loadAggregate)
            .assertNext {
                assertThat(it).isInstanceOf(ExistingTestAggregate::class.java)
                assertThat(it.aggregateId).isEqualTo(aggregateId)
                assertThat((it as ExistingTestAggregate).value.value.contains(updatedValue)).isTrue()
            }
            .verifyComplete()
    }

    @Test
    fun `creation aggregate and update with 100 events, aggregate is updated`() {
        val aggregateId = newId()
        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = aggregateId

        val updateCommands = (1..100).map { i ->
            val updatedValue = "Updated $i"
            UpdateTestValue(aggregateId = aggregateId, value = StringValueParam(updatedValue))
        }

        val executions =
            updateCommands.fold(testAggregateManager.execute(createTestAggregate, aggregateId)) { acc, cmd ->
                acc.then(testAggregateManager.execute(cmd, aggregateId))
            }

        StepVerifier.create(executions)
            .expectNextCount(1)
            .verifyComplete()

        val loadAggregate = testAggregateRepository.find(id).block()

        assertThat(loadAggregate).isNotNull
    }

}