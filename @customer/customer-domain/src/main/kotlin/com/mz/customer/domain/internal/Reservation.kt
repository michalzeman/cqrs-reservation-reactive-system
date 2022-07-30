package com.mz.customer.domain.internal

import com.mz.reservation.common.api.domain.Id

enum class ReservationStatus {
    REQUESTED,
    DECLINED,
    CREATED,
}

data class Reservation(val id: Id, val status: ReservationStatus)

