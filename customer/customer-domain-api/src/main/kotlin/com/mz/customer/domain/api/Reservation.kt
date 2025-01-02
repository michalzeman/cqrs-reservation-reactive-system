package com.mz.customer.domain.api

import com.mz.ddd.common.api.domain.Id
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

enum class ReservationStatus {
    REQUESTED,
    DECLINED,
    CONFIRMED,
}

@Serializable
data class Reservation(
    val requestId: Id,
    val status: ReservationStatus,
    val reservationPeriod: ReservationPeriod,
    val id: Id? = null,
)

@Serializable
data class ReservationPeriod(
    val startTime: Instant,
    val endTime: Instant,
)

