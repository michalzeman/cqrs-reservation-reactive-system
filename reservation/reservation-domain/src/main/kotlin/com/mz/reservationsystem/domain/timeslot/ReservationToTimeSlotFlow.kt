package com.mz.reservationsystem.domain.timeslot

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.reservationsystem.domain.api.reservation.DeclineReservation
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.reservation.ReservationRequested
import com.mz.reservationsystem.domain.api.timeslot.BookTimeSlot
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Component
class ReservationToTimeSlotFlow(
    private val channelStream: ApplicationChannelStream,
    private val timeSlotApi: TimeSlotApi,
    private val timeSlotView: TimeSlotView
) {

    init {
        channelStream.subscribeToChannel(ReservationDocument::class.java, ::invoke)
    }

    operator fun invoke(document: ReservationDocument): Mono<Void> {
        return when {
            document.events.any { it is ReservationRequested } -> handleReservationRequested(document)
            else -> Mono.empty()
        }
    }

    private fun handleReservationRequested(document: ReservationDocument): Mono<Void> {
        val query = FindTimeSlotBetweenTimes(document.startTime, document.endTime)
        val timeSlots = timeSlotView.find(query).singleOrEmpty().cache()
        return timeSlots
            .map { true }
            .switchIfEmpty { false.toMono() }
            .flatMap { exists ->
                if (exists) timeSlots.flatMap { aggregateId ->
                    timeSlotApi.execute(
                        BookTimeSlot(
                            aggregateId = aggregateId,
                            reservationId = document.aggregateId,
                            booked = true,
                            commandId = document.correlationId
                        )
                    ).then()
                }
                else declineReservation(document)
            }
    }

    private fun declineReservation(document: ReservationDocument): Mono<Void> {
        val declineReservation = DeclineReservation(
            aggregateId = document.aggregateId,
            commandId = document.correlationId
        )
        return channelStream.publish(ChannelMessage(declineReservation)).toMono().then()
    }
}
