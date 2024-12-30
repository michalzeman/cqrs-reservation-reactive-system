package com.mz.reservationsystem.aiagent.adapter.reservation

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.adapter.model.reservation.RequestReservationRequest
import com.mz.reservationsystem.aiagent.domain.reservation.CreateReservation
import com.mz.reservationsystem.aiagent.domain.reservation.FindTimeSlotByTimeWindow
import com.mz.reservationsystem.aiagent.domain.reservation.ReservationRepository
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class BackedReservationRepository(
    private val reservationWebClient: WebClient
) : ReservationRepository {
    override fun findTimeSlotByTimeWindow(data: FindTimeSlotByTimeWindow): Flow<TimeSlotDocument> =
        reservationWebClient.get()
            .uri {
                it.path("time-slots")
                    .queryParam("start_time", data.startTime)
                    .queryParam("end_time", data.endTime)
                    .queryParam("booked", false)
                    .build()
            }.retrieve()
            .bodyToFlow<TimeSlotDocument>()

    override suspend fun createReservation(data: CreateReservation): Id {
        val body = RequestReservationRequest(
            customerId = data.customerId.value,
            requestId = uuid(),
            startTime = data.startTime,
            endTime = data.endTime
        )
        return reservationWebClient.post()
            .uri("reservations")
            .bodyValue(body)
            .retrieve()
            .bodyToMono<ReservationDocument>()
            .map { it.aggregateId }
            .awaitSingle()
    }
}