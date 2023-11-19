package com.mz.customer

import com.mz.customer.api.domain.CustomerDocument
import com.mz.customer.api.domain.command.CustomerCommand
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.customer.domain.internal.Customer
import com.mz.customer.domain.internal.CustomerCommandHandler
import com.mz.customer.domain.internal.CustomerEventHandler
import com.mz.customer.domain.internal.getAggregate
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.event.DomainTag
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import reactor.kotlin.core.publisher.toMono


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
            { it.getAggregate().toMono() },
            CustomerCommandHandler(),
            CustomerEventHandler(),
        )
    }

    @Bean("customerEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<CustomerEvent, Customer> {
        TODO("Not yet implemented")
    }

    @Bean("customerDomainTag")
    override fun domainTag(): DomainTag {
        TODO("Not yet implemented")
    }

    @Bean("customerAggregateManager")
    override fun aggregateManager(
        @Qualifier("customerAggregateMapper")
        aggregateRepository: AggregateRepository<Customer, CustomerCommand, CustomerEvent>,
        aggregateMapper: (Customer) -> CustomerDocument
    ): AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument> {
        TODO("Not yet implemented")
    }

    @Bean
    fun aggregateMapper(): (Customer) -> CustomerDocument {
        TODO()
    }

}