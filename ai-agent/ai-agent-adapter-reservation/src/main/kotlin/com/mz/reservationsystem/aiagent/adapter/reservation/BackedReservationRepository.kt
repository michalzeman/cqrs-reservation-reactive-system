package com.mz.reservationsystem.aiagent.adapter.reservation

import com.mz.reservationsystem.aiagent.domain.reservation.FindTimeSlotByTimeWindow
import com.mz.reservationsystem.aiagent.domain.reservation.ReservationRepository
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow

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
}