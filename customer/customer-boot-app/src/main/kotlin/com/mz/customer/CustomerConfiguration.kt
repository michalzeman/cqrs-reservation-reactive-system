package com.mz.customer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.common.components.CommonComponentsConfiguration
import com.mz.common.components.json.registerRequiredModules
import com.mz.customer.domain.api.CustomerCommand
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerEvent
import com.mz.customer.application.internal.CUSTOMER_DOMAIN_TAG
import com.mz.customer.application.internal.Customer
import com.mz.customer.application.internal.CustomerCommandHandler
import com.mz.customer.application.internal.CustomerEventHandler
import com.mz.customer.application.internal.getAggregate
import com.mz.customer.application.internal.toDocument
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import com.mz.ddd.common.view.adapter.cassandradb.wiring.DomainViewConfiguration
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

private const val CUSTOMER_AGGREGATE_REPOSITORY_BEAN = "customerAggregateRepository"
private const val CUSTOMER_AGGREGATE_MANAGER_BEAN = "customerAggregateManager"

@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class,
    DomainViewConfiguration::class,
    CommonComponentsConfiguration::class
)
class CustomerConfiguration(
    private val applicationChannelStream: ApplicationChannelStream
) : AbstractEventSourcingConfiguration<
        Customer,
        CustomerCommand,
        CustomerEvent,
        CustomerDocument>() {

    @Bean(CUSTOMER_AGGREGATE_REPOSITORY_BEAN)
    override fun aggregateRepository(): AggregateRepository<Customer, CustomerCommand, CustomerEvent> {
        return buildAggregateRepository(
            { it.getAggregate() },
            CustomerCommandHandler(),
            CustomerEventHandler(),
        )
    }

    @Bean("customerEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<CustomerEvent, Customer> {
        return JsonEventSerDesAdapter.build()
    }

    @Bean("customerDomainTag")
    override fun domainTag() = CUSTOMER_DOMAIN_TAG

    @Bean(CUSTOMER_AGGREGATE_MANAGER_BEAN)
    override fun aggregateManager(
        @Qualifier(CUSTOMER_AGGREGATE_REPOSITORY_BEAN)
        aggregateRepository: AggregateRepository<Customer, CustomerCommand, CustomerEvent>,
        aggregateMapper: (CommandEffect<Customer, CustomerEvent>) -> CustomerDocument
    ): AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument> {
        return buildAggregateManager(aggregateRepository, aggregateMapper, publishDocument = { doc ->
            mono {
                applicationChannelStream.publish(ChannelMessage(doc))
            }.then()
        })
    }

    @Bean
    fun aggregateMapper(): (CommandEffect<Customer, CustomerEvent>) -> CustomerDocument {
        return { it.aggregate.toDocument(it.events.toSet()) }
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerRequiredModules()
    }
}
