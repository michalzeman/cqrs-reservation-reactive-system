package com.mz.reservationsystem

import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.desJson
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.serToJsonString
import com.mz.reservationsystem.domain.api.reservation.RESERVATION_DOMAIN_TAG
import com.mz.reservationsystem.domain.api.reservation.ReservationCommand
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.reservation.ReservationEvent
import com.mz.reservationsystem.domain.internal.reservation.ReservationAggregate
import com.mz.reservationsystem.domain.internal.reservation.ReservationCommandHandler
import com.mz.reservationsystem.domain.internal.reservation.ReservationEventHandler
import com.mz.reservationsystem.domain.internal.reservation.toAggregate
import com.mz.reservationsystem.domain.internal.reservation.toDocument
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

typealias ReservationEventSourcingConfiguration = AbstractEventSourcingConfiguration<ReservationAggregate, ReservationCommand, ReservationEvent, ReservationDocument>
typealias ReservationAggregateManager = AggregateManager<ReservationAggregate, ReservationCommand, ReservationEvent, ReservationDocument>

@Configuration
class ReservationAggregateConfiguration : ReservationEventSourcingConfiguration() {


    @Bean("reservationAggregateRepository")
    override fun aggregateRepository(): AggregateRepository<ReservationAggregate, ReservationCommand, ReservationEvent> {
        return buildAggregateRepository(
            { id -> id.toAggregate() },
            ReservationCommandHandler(),
            ReservationEventHandler(),
        )
    }

    @Bean("reservationEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<ReservationEvent, ReservationAggregate> {
        return JsonEventSerDesAdapter(
            { serToJsonString(it) },
            { desJson(it) },
            { serToJsonString(it) },
            { desJson(it) }
        )
    }

    override fun domainTag(): DomainTag = RESERVATION_DOMAIN_TAG

    @Bean("reservationAggregateManager")
    override fun aggregateManager(
        @Qualifier("reservationAggregateRepository")
        aggregateRepository: AggregateRepository<ReservationAggregate, ReservationCommand, ReservationEvent>,
        @Qualifier("reservationAggregateMapper")
        aggregateMapper: (CommandEffect<ReservationAggregate, ReservationEvent>) -> ReservationDocument
    ): ReservationAggregateManager {
        return buildAggregateManager(aggregateRepository, aggregateMapper)
    }

    @Bean("reservationAggregateMapper")
    fun aggregateMapper(): (CommandEffect<ReservationAggregate, ReservationEvent>) -> ReservationDocument {
        return { commandEffect -> commandEffect.aggregate.toDocument(commandEffect.events.toSet()) }
    }
}