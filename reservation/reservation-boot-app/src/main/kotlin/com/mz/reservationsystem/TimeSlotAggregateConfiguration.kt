package com.mz.reservationsystem

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

private const val TIME_SLOT_AGGREGATE_REPOSITORY_BEAN = "timeSlotAggregateRepository"
private const val TIME_SLOT_AGGREGATE_MANAGER_BEAN = "timeSlotAggregateManager"


@Configuration
class TimeSlotAggregateConfiguration(
    private val applicationChannelStream: ApplicationChannelStream
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
        return buildAggregateManager(aggregateRepository, aggregateMapper, publishDocument = this::documentPublisher)
    }

    @Bean("timeSlotEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<TimeSlotEvent, TimeSlotAggregate> {
        return JsonEventSerDesAdapter.build()
    }

    override fun domainTag(): DomainTag = TIME_SLOT_DOMAIN_TAG

    @Bean("timeSlotAggregateMapper")
    fun aggregateMapper(): (CommandEffect<TimeSlotAggregate, TimeSlotEvent>) -> TimeSlotDocument {
        return { it.aggregate.toDocument(it.events.toSet()) }
    }

    fun documentPublisher(document: TimeSlotDocument): Mono<Void> {
        return Mono.fromRunnable { applicationChannelStream.publish(ChannelMessage(document)) }
    }
}
