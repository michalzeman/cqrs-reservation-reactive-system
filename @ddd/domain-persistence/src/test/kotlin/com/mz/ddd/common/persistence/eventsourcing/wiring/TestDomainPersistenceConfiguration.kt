package com.mz.ddd.common.persistence.eventsourcing.wiring

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.desJson
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.serToJsonString
import com.mz.ddd.common.persistence.eventsourcing.internal.util.EmptyTestAggregate
import com.mz.ddd.common.persistence.eventsourcing.internal.util.ExistingTestAggregate
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestAggregate
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestCommand
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestCommandHandler
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestDocument
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestEvent
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestEventHandler
import com.mz.ddd.common.persistence.eventsourcing.internal.util.testTag
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class
)
@ActiveProfiles("test")
class TestDomainPersistenceConfiguration :
    AbstractEventSourcingConfiguration<TestAggregate, TestCommand, TestEvent, TestDocument>() {

    @Bean
    fun testAggregateMapper(): (CommandEffect<TestAggregate, TestEvent>) -> TestDocument {
        return {
            when (val agg = it.aggregate) {
                is EmptyTestAggregate -> TestDocument(docId = agg.aggregateId, value = "")
                is ExistingTestAggregate -> TestDocument(
                    docId = agg.aggregateId,
                    value = agg.value.value
                )
            }
        }
    }

    @Bean
    override fun aggregateRepository(): AggregateRepository<TestAggregate, TestCommand, TestEvent> {
        return buildAggregateRepository(
            { EmptyTestAggregate(it) },
            TestCommandHandler(),
            TestEventHandler(),
        )
    }

    @Bean
    override fun aggregateManager(
        aggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>,
        aggregateMapper: (CommandEffect<TestAggregate, TestEvent>) -> TestDocument
    ): AggregateManager<TestAggregate, TestCommand, TestEvent, TestDocument> {
        return buildAggregateManager(aggregateRepository, testAggregateMapper())
    }

    @Bean
    override fun eventSerDesAdapter(): EventSerDesAdapter<TestEvent, TestAggregate> {
        return JsonEventSerDesAdapter(
            { serToJsonString(it) },
            { desJson(it) },
            { serToJsonString(it) },
            { desJson(it) })
    }

    override fun domainTag() = testTag
}
