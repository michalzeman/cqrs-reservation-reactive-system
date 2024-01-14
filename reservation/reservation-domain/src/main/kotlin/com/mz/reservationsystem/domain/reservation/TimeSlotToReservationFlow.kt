package com.mz.reservationsystem.domain.reservation

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.subscribeToChannel
import com.mz.reservationsystem.domain.api.reservation.AcceptReservation
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotBooked
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class TimeSlotToReservationFlow(
    @Qualifier("reservationAggregateManager")
    private val aggregateManager: ReservationAggregateManager,
    private val channelStream: ApplicationChannelStream,
) {

    init {
        channelStream.subscribeToChannel<TimeSlotDocument>(::invoke)
    }

    operator fun invoke(timeSlotDocument: TimeSlotDocument): Mono<Void> {
        return when {
            timeSlotDocument.events.any { it is TimeSlotBooked } -> handleTimeSlotBooked(timeSlotDocument)
            else -> Mono.empty()
        }
    }

    private fun handleTimeSlotBooked(document: TimeSlotDocument): Mono<Void> {
        return (document.reservationId?.toMono() ?: Mono.empty())
            .flatMap { aggregateId ->
                val acceptReservation = AcceptReservation(
                    aggregateId = aggregateId,
                    correlationId = document.correlationId,
                    timeSlotId = document.aggregateId
                )
                aggregateManager.execute(acceptReservation, aggregateId) {
                    aggregateManager.checkExistence(aggregateId)
                }
            }.then()
    }
}