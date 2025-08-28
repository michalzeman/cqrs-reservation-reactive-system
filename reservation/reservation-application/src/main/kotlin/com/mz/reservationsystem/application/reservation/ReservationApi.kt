package com.mz.reservationsystem.application.reservation

import com.mz.common.components.ApplicationChannelStream
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.reservationsystem.domain.api.reservation.ReservationCommand
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.reservation.ReservationEvent
import com.mz.reservationsystem.application.internal.reservation.ReservationAggregate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

typealias ReservationAggregateManager = AggregateManager<ReservationAggregate, ReservationCommand, ReservationEvent, ReservationDocument>

@Component
class ReservationApi(
    @Qualifier("reservationAggregateManager")
    private val aggregateManager: ReservationAggregateManager,
    private val applicationChannelStream: ApplicationChannelStream
) {

    init {
        applicationChannelStream.subscribeToChannel(ReservationCommand::class.java, ::execute)
    }

    fun execute(cmd: ReservationCommand): Mono<ReservationDocument> {
        return aggregateManager.execute(cmd, cmd.aggregateId)
    }

    fun findById(id: Id): Mono<ReservationDocument> {
        return aggregateManager.findById(id)
    }
}