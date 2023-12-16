package com.mz.customer.domain.api

import com.mz.customer.domain.api.event.CustomerReservationConfirmed
import com.mz.customer.domain.api.event.CustomerReservationDeclined
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

fun Set<Reservation>.apply(event: CustomerReservationConfirmed): Set<Reservation> {
    val reservations = this.filterNot { it.id == event.requestId }
    return reservations.plus(Reservation(event.reservationId, status = ReservationStatus.CONFIRMED)).toSet()
}

fun Set<Reservation>.apply(event: CustomerReservationDeclined): Set<Reservation> {
    val reservations = this.filterNot { it.id == event.requestId || it.id == event.reservationId }
    return reservations.plus(Reservation(event.reservationId, status = ReservationStatus.DECLINED)).toSet()
}

