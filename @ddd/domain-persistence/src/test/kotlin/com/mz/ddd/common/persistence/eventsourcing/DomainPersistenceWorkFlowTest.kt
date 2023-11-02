package com.mz.ddd.common.persistence.eventsourcing

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.uuid
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
    lateinit var testDomainManager: DomainManager<TestAggregate, TestCommand, TestEvent, TestDocument>

    @Autowired
    lateinit var testAggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>

    @Test
    fun `should execute command for creation of aggregate, aggregate is created`() {
        val aggregateId = uuid()
        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = Id(aggregateId)

        val aggregate = testDomainManager.execute(createTestAggregate, aggregateId)

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
        val aggregateId = uuid()
        val stringInitValue = StringValueParam("Hello there\n")
        val createTestAggregate = CreateTestAggregate(value = stringInitValue)
        val id = Id(aggregateId)

        val updatedValue = "I am now updated"
        val updateTestAggregate = UpdateTestValue(aggregateId = aggregateId, value = StringValueParam(updatedValue))

        val aggregate = testDomainManager.execute(createTestAggregate, aggregateId)
            .then(testDomainManager.execute(updateTestAggregate, aggregateId))

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

}