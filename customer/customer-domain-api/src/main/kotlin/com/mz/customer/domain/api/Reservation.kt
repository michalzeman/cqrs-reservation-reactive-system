package com.mz.customer.domain.api

import com.mz.customer.domain.api.ReservationStatus.CONFIRMED
import com.mz.customer.domain.api.ReservationStatus.DECLINED
import com.mz.ddd.common.api.domain.Id
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

enum class ReservationStatus {
    REQUESTED,
    DECLINED,
    CONFIRMED,
}

@Serializable
data class Reservation(val id: Id, val status: ReservationStatus, val reservationPeriod: ReservationPeriod)

@Serializable
data class ReservationPeriod(
    val startTime: Instant,
    val endTime: Instant,
)

fun Set<Reservation>.existsReservation(id: Id) = this.any { it.id == id }

fun Set<Reservation>.apply(event: CustomerReservationConfirmed): Set<Reservation> {
    val reservationToBeUpdated = this.find { it.id == event.requestId }!!
    val reservations = this.filterNot { it.id == event.requestId }
    return reservations.plus(reservationToBeUpdated.copy(id = event.reservationId, status = CONFIRMED)).toSet()
}

fun Set<Reservation>.apply(event: CustomerReservationDeclined): Set<Reservation> {
    val reservationToBeUpdated = this.find { it.id == event.requestId || it.id == event.reservationId }!!
    val reservations = this.filterNot { it.id == event.requestId || it.id == event.reservationId }
    return reservations.plus(reservationToBeUpdated.copy(id = event.reservationId, status = DECLINED)).toSet()
}

