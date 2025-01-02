package com.mz.customer.domain.api.command

import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.domain.api.RequestNewCustomerReservation
import com.mz.customer.domain.api.ReservationPeriod
import com.mz.customer.domain.api.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.api.UpdateCustomerReservationAsDeclined
import com.mz.customer.domain.api.toEvent
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomerCommandsApiTest {

    @Test
    fun `RegisterCustomer command should correctly convert to CustomerRegistered event`() {
        val command = RegisterCustomer(LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val event = command.toEvent(Id("1"))
        assertEquals("Doe", event.lastName.value)
        assertEquals("John", event.firstName.value)
        assertEquals("john.doe@example.com", event.email.value)
        assertEquals("1", event.aggregateId.value)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `RequestNewCustomerReservation command should correctly convert to CustomerReservationRequested event`() {
        val command =
            RequestNewCustomerReservation(Id("1"), Id("2"), ReservationPeriod(instantNow(), instantNow()), newId())
        val event = command.toEvent()
        assertEquals("1", event.aggregateId.value)
        assertEquals("2", event.requestId.value)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `UpdateCustomerReservationAsConfirmed command should correctly convert to CustomerReservationConfirmed event`() {
        val command = UpdateCustomerReservationAsConfirmed(Id("1"), Id("2"), newId())
        val event = command.toEvent()
        assertEquals("1", event.aggregateId.value)
        assertEquals("2", event.reservationId.value)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `UpdateCustomerReservationAsDeclined command should correctly convert to CustomerReservationDeclined event`() {
        val command = UpdateCustomerReservationAsDeclined(Id("1"), Id("2"), newId())
        val event = command.toEvent()
        assertEquals("1", event.aggregateId.value)
        assertEquals("2", event.reservationId.value)
        assertEquals(event.correlationId, command.correlationId)
    }
}