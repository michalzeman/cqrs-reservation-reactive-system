package com.mz.customer.domain.api

import com.mz.ddd.common.api.domain.Id
import kotlinx.serialization.Serializable

enum class ReservationStatus {
    REQUESTED,
    DECLINED,
    CONFIRMED,
}

@Serializable
data class Reservation(val id: Id, val status: ReservationStatus)

fun Set<Reservation>.existsReservation(id: Id) = this.any { it.id == id }

