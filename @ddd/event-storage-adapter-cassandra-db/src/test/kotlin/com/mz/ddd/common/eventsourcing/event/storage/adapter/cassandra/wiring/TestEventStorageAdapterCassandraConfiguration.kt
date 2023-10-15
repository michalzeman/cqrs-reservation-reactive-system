package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.EventJournalRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@EnableReactiveCassandraRepositories(basePackageClasses = [EventJournalRepository::class])
class TestEventStorageAdapterCassandraConfiguration(
    @Value("\${spring.data.cassandra.keyspace-name}") val keySpace: String
) : AbstractReactiveCassandraConfiguration() {

    override fun getKeyspaceName(): String {
        return keySpace
    }
}