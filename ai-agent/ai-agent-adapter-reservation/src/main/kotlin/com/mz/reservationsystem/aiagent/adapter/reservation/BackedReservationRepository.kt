package com.mz.reservationsystem.aiagent.adapter.reservation

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.application.reservation.FindTimeSlotByTimeWindow
import com.mz.reservationsystem.aiagent.application.reservation.ReservationData
import com.mz.reservationsystem.aiagent.application.reservation.ReservationRepository
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux

@Component
class BackedReservationRepository(
    private val reservationWebClient: WebClient
) : ReservationRepository {
    override fun findTimeSlotByTimeWindow(data: FindTimeSlotByTimeWindow): Flow<TimeSlotDocument> =
        reservationWebClient.get()
            .uri {
                it.path("/time-slots")
                    .queryParam("start_time", data.startTime)
                    .queryParam("end_time", data.endTime)
                    .queryParam("booked", false)
                    .build()
            }.retrieve()
            .bodyToMono<List<TimeSlotDocument>>()
            .flatMapMany { Flux.fromIterable(it) }
            .asFlow()

    override suspend fun getReservationById(id: Id): ReservationData? {
        return reservationWebClient.get()
            .uri("/reservations/${id.value}")
            .retrieve()
            .bodyToMono<ReservationDocument>()
            .map { ReservationData(it) }
            .awaitSingleOrNull()
    }
}