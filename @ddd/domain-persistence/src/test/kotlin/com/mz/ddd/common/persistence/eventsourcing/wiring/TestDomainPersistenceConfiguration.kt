package com.mz.ddd.common.persistence.eventsourcing.wiring

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.EventStorageAdapter
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.DataStorageAdaptersConfig
import com.mz.ddd.common.persistence.eventsourcing.DomainPersistenceFactory
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.desJson
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.serToJsonString
import com.mz.ddd.common.persistence.eventsourcing.internal.util.*
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockStorageAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Configuration
@Import(EventStorageAdapterCassandraConfiguration::class, RedisLockStorageAdapterConfiguration::class)
@ActiveProfiles("test")
class TestDomainPersistenceConfiguration {

    @Bean
    fun dataStorageAdaptersConfig(
        eventStorageAdapter: EventStorageAdapter, lockStorageAdapter: LockStorageAdapter
    ) = DataStorageAdaptersConfig(
        eventStorageAdapter,
        JsonEventSerDesAdapter<TestEvent>({ serToJsonString(it) }, { desJson(it) }),
        lockStorageAdapter
    )

    @Bean
    fun testAggregateRepository(
        dataStorageAdaptersConfig: DataStorageAdaptersConfig<TestEvent>
    ): AggregateRepository<TestAggregate, TestCommand, TestEvent> {
        return DomainPersistenceFactory.buildAggregateRepository(
            testTag,
            { EmptyTestAggregate(it) },
            TestCommandHandler(),
            TestEventHandler(),
            dataStorageAdaptersConfig
        )
    }

    @Bean
    fun testDomainManager(
        testAggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>,
        testAggregateMapper: (TestAggregate) -> TestDocument
    ): AggregateManager<TestAggregate, TestCommand, TestEvent, TestDocument> {
        return DomainPersistenceFactory.buildAggregateManager(testAggregateRepository, testAggregateMapper)
    }

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

}