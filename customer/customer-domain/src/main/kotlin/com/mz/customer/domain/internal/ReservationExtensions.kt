package com.mz.customer.domain.internal

import com.mz.customer.domain.api.CustomerReservationConfirmed
import com.mz.customer.domain.api.CustomerReservationDeclined
import com.mz.customer.domain.api.Reservation
import com.mz.customer.domain.api.ReservationStatus.CONFIRMED
import com.mz.customer.domain.api.ReservationStatus.DECLINED
import com.mz.ddd.common.api.domain.Id

internal fun Set<Reservation>.existsReservation(id: Id) = this.any { it.requestId == id }

internal fun Set<Reservation>.apply(event: CustomerReservationConfirmed): Set<Reservation> {
    val reservationToBeUpdated = this.find { it.requestId == event.requestId }!!
    val reservations = this.filterNot { it.requestId == event.requestId }
    return reservations.plus(reservationToBeUpdated.copy(id = event.reservationId, status = CONFIRMED)).toSet()
}

internal fun Set<Reservation>.apply(event: CustomerReservationDeclined): Set<Reservation> {
    val reservationToBeUpdated = this.find { it.requestId == event.requestId || it.id == event.reservationId }!!
    val reservations = this.filterNot { it.requestId == event.requestId || it.id == event.reservationId }
    return reservations.plus(reservationToBeUpdated.copy(id = event.reservationId, status = DECLINED)).toSet()
}