package com.mz.customer

import com.mz.customer.api.domain.CustomerDocument
import com.mz.customer.api.domain.command.CustomerCommand
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.customer.domain.internal.*
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.desJson
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.serToJsonString
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class
)
class CustomerConfiguration : AbstractEventSourcingConfiguration<
        Customer,
        CustomerCommand,
        CustomerEvent,
        CustomerDocument>() {

    @Bean("customerAggregateMapper")
    override fun aggregateRepository(): AggregateRepository<Customer, CustomerCommand, CustomerEvent> {
        return buildAggregateRepository(
            { it.getAggregate() },
            CustomerCommandHandler(),
            CustomerEventHandler(),
        )
    }

    @Bean("customerEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<CustomerEvent, Customer> {
        return JsonEventSerDesAdapter(
            { serToJsonString(it) },
            { desJson(it) },
            { serToJsonString(it) },
            { desJson(it) }
        )
    }

    @Bean("customerDomainTag")
    override fun domainTag() = CUSTOMER_DOMAIN_TAG

    @Bean("customerAggregateManager")
    override fun aggregateManager(
        @Qualifier("customerAggregateMapper")
        aggregateRepository: AggregateRepository<Customer, CustomerCommand, CustomerEvent>,
        aggregateMapper: (CommandEffect<Customer, CustomerEvent>) -> CustomerDocument
    ): AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument> {
        return buildAggregateManager(aggregateRepository, aggregateMapper)
    }

    @Bean
    fun aggregateMapper(): (CommandEffect<Customer, CustomerEvent>) -> CustomerDocument {
        return { it.aggregate.toDocument(it.events.toSet()) }
    }

}