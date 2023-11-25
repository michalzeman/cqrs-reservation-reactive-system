package com.mz.customer.domain.internal

import com.mz.customer.api.domain.event.CustomerRegistered
import com.mz.customer.api.domain.event.CustomerReservationConfirmed
import com.mz.customer.api.domain.event.CustomerReservationDeclined
import com.mz.customer.api.domain.event.CustomerReservationRequested
import com.mz.ddd.common.api.domain.*
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
        val event = CustomerReservationRequested(Id("1"), Id("2"))
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
        val event = CustomerReservationRequested(Id("1"), Id("2"))
        val result = eventHandler.apply(existingCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(3))
    }

    @Test
    fun `applying CustomerReservationConfirmed event to ExistingCustomer should return ExistingCustomer`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(2), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val event = CustomerReservationConfirmed(Id("1"), Id("2"))
        val result = eventHandler.apply(existingCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(3))
    }

    @Test
    fun `applying CustomerReservationDeclined event to ExistingCustomer should return ExistingCustomer`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(2), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val event = CustomerReservationDeclined(Id("1"), Id("2"))
        val result = eventHandler.apply(existingCustomer, event)
        assertTrue(result is ExistingCustomer)
        assertEquals(result.version, Version(3))
    }
}