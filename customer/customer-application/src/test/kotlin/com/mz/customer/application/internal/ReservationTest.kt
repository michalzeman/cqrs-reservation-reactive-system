package com.mz.customer.application.internal

import com.mz.customer.domain.api.Reservation
import com.mz.customer.domain.api.ReservationPeriod
import com.mz.customer.domain.api.ReservationStatus
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReservationTest {

    @Test
    fun `should correctly identify existing reservation`() {
        val reservations =
            setOf(Reservation(Id("1"), ReservationStatus.REQUESTED, ReservationPeriod(instantNow(), instantNow())))
        assertTrue(reservations.existsReservation(Id("1")))
    }

    @Test
    fun `should correctly identify non-existing reservation`() {
        val reservations =
            setOf(Reservation(Id("1"), ReservationStatus.REQUESTED, ReservationPeriod(instantNow(), instantNow())))
        assertFalse(reservations.existsReservation(Id("2")))
    }

    @Test
    fun `should correctly handle empty set`() {
        val reservations = emptySet<Reservation>()
        assertFalse(reservations.existsReservation(Id("1")))
    }
}