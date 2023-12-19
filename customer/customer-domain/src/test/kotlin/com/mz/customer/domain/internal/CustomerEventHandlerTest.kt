package com.mz.customer.domain.internal

import com.mz.customer.domain.api.CustomerRegistered
import com.mz.customer.domain.api.CustomerReservationConfirmed
import com.mz.customer.domain.api.CustomerReservationDeclined
import com.mz.customer.domain.api.CustomerReservationRequested
import com.mz.customer.domain.api.Reservation
import com.mz.customer.domain.api.ReservationPeriod
import com.mz.customer.domain.api.ReservationStatus
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.Version
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CustomerEventHandlerTest {

    private val eventHandler = CustomerEventHandler()

    @Test
    fun `applying CustomerRegistered event to EmptyCustomer should return ExistingCustomer`() {
        val emptyCustomer = EmptyCustomer(Id("1"))
        val event = CustomerRegistered(Id("1"), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val result = eventHandler.apply(emptyCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(0))
    }

    @Test
    fun `applying other events to EmptyCustomer should throw an exception`() {
        val emptyCustomer = EmptyCustomer(Id("1"))
        val event = CustomerReservationRequested(Id("1"), ReservationPeriod(instantNow(), instantNow()), Id("2"))
        assertThrows<RuntimeException> {
            eventHandler.apply(emptyCustomer, event)
        }
    }

    @Test
    fun `applying CustomerRegistered event to ExistingCustomer should throw an exception`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(2), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val event = CustomerRegistered(Id("1"), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        assertThrows<RuntimeException> {
            eventHandler.apply(existingCustomer, event)
        }
    }

    @Test
    fun `applying CustomerReservationRequested event to ExistingCustomer should return ExistingCustomer`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(2), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val event = CustomerReservationRequested(Id("1"), ReservationPeriod(instantNow(), instantNow()), Id("2"))
        val result = eventHandler.apply(existingCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(3))
    }

    @Test
    fun `applying CustomerReservationConfirmed event to ExistingCustomer should return ExistingCustomer`() {
        val existingReservation =
            Reservation(Id("reservation:2"), ReservationStatus.REQUESTED, ReservationPeriod(instantNow(), instantNow()))
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(2),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
                setOf(existingReservation)
            )
        val event = CustomerReservationConfirmed(Id("1"), Id("reservation:2"), newId())
        val result = eventHandler.apply(existingCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(3))
        assertTrue((result as ExistingCustomer).reservations.all { it.status == ReservationStatus.CONFIRMED })
        assertTrue(result.reservations.all { it.id == Id("1") })
    }

    @Test
    fun `applying CustomerReservationDeclined event to ExistingCustomer should return ExistingCustomer`() {
        val existingReservation =
            Reservation(Id("reservation:2"), ReservationStatus.REQUESTED, ReservationPeriod(instantNow(), instantNow()))
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(2),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
                setOf(existingReservation)
            )
        val event = CustomerReservationDeclined(Id("1"), Id("reservation:2"), newId())
        val result = eventHandler.apply(existingCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(3))
        assertTrue((result as ExistingCustomer).reservations.all { it.status == ReservationStatus.DECLINED })
        assertTrue(result.reservations.all { it.id == Id("1") })
    }
}