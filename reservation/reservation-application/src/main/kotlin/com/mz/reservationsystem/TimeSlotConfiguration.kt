package com.mz.reservationsystem

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.json.registerRequiredModules
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.desJson
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.serToJsonString
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import com.mz.ddd.common.query.wiring.DomainViewConfiguration
import com.mz.reservationsystem.domain.api.timeslot.TIME_SLOT_DOMAIN_TAG
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCommand
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotEvent
import com.mz.reservationsystem.domain.internal.timeslot.TimeSlotAggregate
import com.mz.reservationsystem.domain.internal.timeslot.TimeSlotCommandHandler
import com.mz.reservationsystem.domain.internal.timeslot.TimeSlotEventHandler
import com.mz.reservationsystem.domain.internal.timeslot.getAggregate
import com.mz.reservationsystem.domain.internal.timeslot.toDocument
import com.mz.reservationsystem.domain.timeslot.TimeSlotAggregateManager
import com.mz.reservationsystem.domain.timeslot.TimeSlotView
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

private const val TIME_SLOT_AGGREGATE_REPOSITORY_BEAN = "timeSlotAggregateRepository"
private const val TIME_SLOT_AGGREGATE_MANAGER_BEAN = "timeSlotAggregateManager"


@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class,
    DomainViewConfiguration::class
)
@ComponentScan("com.mz.reservationsystem.domain.**")
class TimeSlotConfiguration(
    private val timeSlotView: TimeSlotView
) :
    AbstractEventSourcingConfiguration<TimeSlotAggregate, TimeSlotCommand, TimeSlotEvent, TimeSlotDocument>() {

    @Bean(TIME_SLOT_AGGREGATE_REPOSITORY_BEAN)
    override fun aggregateRepository(): AggregateRepository<TimeSlotAggregate, TimeSlotCommand, TimeSlotEvent> {
        return buildAggregateRepository(
            { it.getAggregate() },
            TimeSlotCommandHandler(),
            TimeSlotEventHandler(),
        )
    }

    @Bean(TIME_SLOT_AGGREGATE_MANAGER_BEAN)
    override fun aggregateManager(
        @Qualifier(TIME_SLOT_AGGREGATE_REPOSITORY_BEAN)
        aggregateRepository: AggregateRepository<TimeSlotAggregate, TimeSlotCommand, TimeSlotEvent>,
        @Qualifier("timeSlotAggregateMapper")
        aggregateMapper: (CommandEffect<TimeSlotAggregate, TimeSlotEvent>) -> TimeSlotDocument
    ): TimeSlotAggregateManager {
        return buildAggregateManager(aggregateRepository, aggregateMapper, publishDocument = timeSlotView::process)
    }

    @Bean("timeSlotEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<TimeSlotEvent, TimeSlotAggregate> {
        return JsonEventSerDesAdapter(
            { serToJsonString(it) },
            { desJson(it) },
            { serToJsonString(it) },
            { desJson(it) }
        )
    }

    override fun domainTag(): DomainTag = TIME_SLOT_DOMAIN_TAG

    @Bean("timeSlotAggregateMapper")
    fun aggregateMapper(): (CommandEffect<TimeSlotAggregate, TimeSlotEvent>) -> TimeSlotDocument {
        return { it.aggregate.toDocument(it.events.toSet()) }
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerRequiredModules()
    }
}