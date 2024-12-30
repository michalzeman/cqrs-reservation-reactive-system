package com.mz.reservationsystem.aiagent.domain.reservation

import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.coroutines.flow.Flow
import java.time.Instant


data class FindTimeSlotByTimeWindow(val startTime: Instant, val endTime: Instant)
data class CreateReservation(val customerId: Id, val email: Email, val startTime: Instant, val endTime: Instant)

interface ReservationRepository {

    fun findTimeSlotByTimeWindow(data: FindTimeSlotByTimeWindow): Flow<TimeSlotDocument>

    suspend fun createReservation(data: CreateReservation): Id
}