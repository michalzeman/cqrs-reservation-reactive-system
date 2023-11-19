package com.mz.ddd.common.persistence.eventsourcing.wiring

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.desJson
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.serToJsonString
import com.mz.ddd.common.persistence.eventsourcing.internal.util.*
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.kotlin.core.publisher.toMono

@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class
)
@ActiveProfiles("test")
class TestDomainPersistenceConfiguration :
    AbstractEventSourcingConfiguration<TestAggregate, TestCommand, TestEvent, TestDocument>() {

    @Bean
    fun testAggregateMapper(): (TestAggregate) -> TestDocument {
        return {
            when (it) {
                is EmptyTestAggregate -> TestDocument(docId = it.aggregateId, value = "")
                is ExistingTestAggregate -> TestDocument(
                    docId = it.aggregateId,
                    value = it.value.value
                )
            }
        }
    }

    @Bean
    override fun aggregateRepository(): AggregateRepository<TestAggregate, TestCommand, TestEvent> {
        return buildAggregateRepository(
            { EmptyTestAggregate(it).toMono() },
            TestCommandHandler(),
            TestEventHandler(),
        )
    }

    @Bean
    override fun aggregateManager(
        aggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>,
        aggregateMapper: (TestAggregate) -> TestDocument
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
