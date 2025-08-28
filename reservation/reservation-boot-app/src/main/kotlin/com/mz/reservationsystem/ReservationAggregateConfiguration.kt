package com.mz.reservationsystem

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.persistence.eventsourcing.AbstractEventSourcingConfiguration
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.ddd.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.json.JsonEventSerDesAdapter
import com.mz.reservationsystem.domain.api.reservation.RESERVATION_DOMAIN_TAG
import com.mz.reservationsystem.domain.api.reservation.ReservationCommand
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.reservation.ReservationEvent
import com.mz.reservationsystem.application.internal.reservation.ReservationAggregate
import com.mz.reservationsystem.application.internal.reservation.ReservationCommandHandler
import com.mz.reservationsystem.application.internal.reservation.ReservationEventHandler
import com.mz.reservationsystem.application.internal.reservation.toAggregate
import com.mz.reservationsystem.application.internal.reservation.toDocument
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

typealias ReservationEventSourcingConfiguration = AbstractEventSourcingConfiguration<ReservationAggregate, ReservationCommand, ReservationEvent, ReservationDocument>
typealias ReservationAggregateManager = AggregateManager<ReservationAggregate, ReservationCommand, ReservationEvent, ReservationDocument>
typealias ReservationAggregateRepository = AggregateRepository<ReservationAggregate, ReservationCommand, ReservationEvent>

@Configuration
class ReservationAggregateConfiguration(
    private val applicationChannelStream: ApplicationChannelStream
) : ReservationEventSourcingConfiguration() {


    @Bean("reservationAggregateRepository")
    override fun aggregateRepository(): ReservationAggregateRepository {
        return buildAggregateRepository(
            { id -> id.toAggregate() },
            ReservationCommandHandler(),
            ReservationEventHandler(),
        )
    }

    @Bean("reservationEventSerDesAdapter")
    override fun eventSerDesAdapter(): EventSerDesAdapter<ReservationEvent, ReservationAggregate> {
        return JsonEventSerDesAdapter.build()
    }

    override fun domainTag(): DomainTag = RESERVATION_DOMAIN_TAG

    @Bean("reservationAggregateManager")
    override fun aggregateManager(
        @Qualifier("reservationAggregateRepository")
        aggregateRepository: ReservationAggregateRepository,
        @Qualifier("reservationAggregateMapper")
        aggregateMapper: (CommandEffect<ReservationAggregate, ReservationEvent>) -> ReservationDocument
    ): ReservationAggregateManager {
        return buildAggregateManager(
            aggregateRepository,
            aggregateMapper,
            publishDocument = this::reservationDocumentPublishing
        )
    }

    @Bean("reservationAggregateMapper")
    fun aggregateMapper(): (CommandEffect<ReservationAggregate, ReservationEvent>) -> ReservationDocument {
        return { commandEffect -> commandEffect.aggregate.toDocument(commandEffect.events.toSet()) }
    }

    private fun reservationDocumentPublishing(document: ReservationDocument): Mono<Void> {
        return Mono.fromRunnable { applicationChannelStream.publish(ChannelMessage(document)) }
    }
}