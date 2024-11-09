package com.mz.reservationsystem.aiagent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.common.components.CommonComponentsConfiguration
import com.mz.common.components.json.registerRequiredModules
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import com.mz.ddd.common.view.adapter.cassandradb.wiring.DomainViewConfiguration
import com.mz.reservationsystem.aiagent.adapter.customer.wiring.CustomerAdapterConfiguration
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatCommand
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatDocument
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatEvent
import com.mz.reservationsystem.aiagent.domain.chat.aggregate.*
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

typealias ChatAggregateRepository = AggregateRepository<Chat, ChatCommand, ChatEvent>

@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class,
    DomainViewConfiguration::class,
    CommonComponentsConfiguration::class,
    CustomerAdapterConfiguration::class,
)
class AiAgentConfiguration(
    private val applicationChannelStream: ApplicationChannelStream
) : AbstractEventSourcingConfiguration<
        Chat,
        ChatCommand,
        ChatEvent,
        ChatDocument>() {

    @Bean("chatAggregateRepository")
    override fun aggregateRepository(): ChatAggregateRepository {
        return buildAggregateRepository(
            { it.getAggregate() },
            ChatCommandHandler(),
            ChatEventHandler(),
        )
    }

    @Bean("chatEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<ChatEvent, Chat> {
        return JsonEventSerDesAdapter.build()
    }

    @Bean("chatDomainTag")
    override fun domainTag(): DomainTag {
        return CHAT_DOMAIN_TAG
    }

    @Bean("chatAggregateManager")
    override fun aggregateManager(
        @Qualifier("chatAggregateRepository")
        aggregateRepository: AggregateRepository<Chat, ChatCommand, ChatEvent>,
        @Qualifier("aggregateMapper")
        aggregateMapper: (CommandEffect<Chat, ChatEvent>) -> ChatDocument
    ): AggregateManager<Chat, ChatCommand, ChatEvent, ChatDocument> {
        return buildAggregateManager(aggregateRepository, aggregateMapper, publishDocument = { doc ->
            mono {
                applicationChannelStream.publish(ChannelMessage(doc))
            }.then()
        })
    }

    @Bean("aggregateMapper")
    fun aggregateMapper(): (CommandEffect<Chat, ChatEvent>) -> ChatDocument {
        return { it.aggregate.toDocument(it.events.toSet()) }
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerRequiredModules()
    }

}