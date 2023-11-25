package com.mz.customer.api.domain.command

import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomerCommandTest {

    @Test
    fun `RegisterCustomer command should correctly convert to CustomerRegistered event`() {
        val command = RegisterCustomer(LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val event = command.toEvent(Id("1"))
        assertEquals("Doe", event.lastName.value)
        assertEquals("John", event.firstName.value)
        assertEquals("john.doe@example.com", event.email.value)
        assertEquals("1", event.customerId.value)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `RequestNewCustomerReservation command should correctly convert to CustomerReservationRequested event`() {
        val command = RequestNewCustomerReservation(Id("1"), Id("2"))
        val event = command.toEvent()
        assertEquals("1", event.customerId.value)
        assertEquals("2", event.reservationId.value)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `UpdateCustomerReservationAsConfirmed command should correctly convert to CustomerReservationConfirmed event`() {
        val command = UpdateCustomerReservationAsConfirmed(Id("1"), Id("2"))
        val event = command.toEvent()
        assertEquals("1", event.customerId.value)
        assertEquals("2", event.reservationId.value)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `UpdateCustomerReservationAsDeclined command should correctly convert to CustomerReservationDeclined event`() {
        val command = UpdateCustomerReservationAsDeclined(Id("1"), Id("2"))
        val event = command.toEvent()
        assertEquals("1", event.customerId.value)
        assertEquals("2", event.reservationId.value)
        assertEquals(event.correlationId, command.correlationId)
    }
}