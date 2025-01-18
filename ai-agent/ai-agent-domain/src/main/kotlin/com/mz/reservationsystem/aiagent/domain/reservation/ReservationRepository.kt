package com.mz.reservationsystem.aiagent.domain.reservation

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.coroutines.flow.Flow
import java.time.Instant


data class FindTimeSlotByTimeWindow(val startTime: Instant, val endTime: Instant)

data class ReservationData(val data: ReservationDocument)

interface ReservationRepository {

    fun findTimeSlotByTimeWindow(data: FindTimeSlotByTimeWindow): Flow<TimeSlotDocument>

    suspend fun getReservationById(id: Id): ReservationData?
}