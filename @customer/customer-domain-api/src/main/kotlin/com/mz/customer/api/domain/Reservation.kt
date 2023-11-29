package com.mz.customer.api.domain

import com.mz.ddd.common.api.domain.Id

enum class ReservationStatus {
    REQUESTED,
    DECLINED,
    CREATED,
}

data class Reservation(val id: Id, val status: ReservationStatus)

fun Set<Reservation>.existsReservation(id: Id) = this.any { it.id == id }

