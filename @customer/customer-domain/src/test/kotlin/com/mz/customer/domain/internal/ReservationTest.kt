package com.mz.customer.domain.internal

import com.mz.customer.api.domain.Reservation
import com.mz.customer.api.domain.ReservationStatus
import com.mz.customer.api.domain.existsReservation
import com.mz.ddd.common.api.domain.Id
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReservationTest {

    @Test
    fun `should correctly identify existing reservation`() {
        val reservations = setOf(Reservation(Id("1"), ReservationStatus.REQUESTED))
        assertTrue(reservations.existsReservation(Id("1")))
    }

    @Test
    fun `should correctly identify non-existing reservation`() {
        val reservations = setOf(Reservation(Id("1"), ReservationStatus.REQUESTED))
        assertFalse(reservations.existsReservation(Id("2")))
    }

    @Test
    fun `should correctly handle empty set`() {
        val reservations = emptySet<Reservation>()
        assertFalse(reservations.existsReservation(Id("1")))
    }
}