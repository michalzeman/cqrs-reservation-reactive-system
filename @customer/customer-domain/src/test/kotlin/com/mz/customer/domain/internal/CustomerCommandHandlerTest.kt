package com.mz.customer.domain.internal

import com.mz.customer.api.domain.Reservation
import com.mz.customer.api.domain.ReservationStatus
import com.mz.customer.api.domain.command.RegisterCustomer
import com.mz.customer.api.domain.command.RequestNewCustomerReservation
import com.mz.customer.api.domain.command.UpdateCustomerReservationAsConfirmed
import com.mz.customer.api.domain.command.UpdateCustomerReservationAsDeclined
import com.mz.customer.api.domain.event.CustomerRegistered
import com.mz.customer.api.domain.event.CustomerReservationConfirmed
import com.mz.customer.api.domain.event.CustomerReservationDeclined
import com.mz.customer.api.domain.event.CustomerReservationRequested
import com.mz.ddd.common.api.domain.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CustomerCommandHandlerTest {

    private val commandHandler = CustomerCommandHandler()

    @Test
    fun `registering a new customer should produce a CustomerRegistered event`() {
        val emptyCustomer = EmptyCustomer(Id("1"))
        val registerCustomerCommand =
            RegisterCustomer(LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val result = commandHandler.execute(emptyCustomer, registerCustomerCommand)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().single() is CustomerRegistered)
    }

    @Test
    fun `registering an existing customer should throw an exception`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(1), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val registerCustomerCommand =
            RegisterCustomer(LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val result = commandHandler.execute(existingCustomer, registerCustomerCommand)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `requesting a new reservation should produce a CustomerReservationRequested event`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(2), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val requestNewCustomerReservationCommand = RequestNewCustomerReservation(Id("1"), Id("2"))
        val result = commandHandler.execute(existingCustomer, requestNewCustomerReservationCommand)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().single() is CustomerReservationRequested)
    }

    @Test
    fun `confirming a reservation should produce a CustomerReservationConfirmed event`() {
        val reservation = Reservation(Id("2"), ReservationStatus.REQUESTED)
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(2),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
                setOf(reservation)
            )
        val updateCustomerReservationAsConfirmedCommand = UpdateCustomerReservationAsConfirmed(Id("1"), Id("2"))
        val result = commandHandler.execute(existingCustomer, updateCustomerReservationAsConfirmedCommand)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().single() is CustomerReservationConfirmed)
    }

    @Test
    fun `confirming a reservation should produce a error`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(2), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val updateCustomerReservationAsConfirmedCommand = UpdateCustomerReservationAsConfirmed(Id("1"), Id("2"))
        val result = commandHandler.execute(existingCustomer, updateCustomerReservationAsConfirmedCommand)
        assertTrue(result.isFailure)
    }

    @Test
    fun `declining a reservation should produce a CustomerReservationDeclined event`() {
        val reservation = Reservation(Id("2"), ReservationStatus.REQUESTED)
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(3),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
                setOf(reservation)
            )
        val updateCustomerReservationAsDeclinedCommand = UpdateCustomerReservationAsDeclined(Id("1"), Id("2"))
        val result = commandHandler.execute(existingCustomer, updateCustomerReservationAsDeclinedCommand)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().single() is CustomerReservationDeclined)
    }

    @Test
    fun `declining a reservation should produce error`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(3), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val updateCustomerReservationAsDeclinedCommand = UpdateCustomerReservationAsDeclined(Id("1"), Id("2"))
        val result = commandHandler.execute(existingCustomer, updateCustomerReservationAsDeclinedCommand)
        assertTrue(result.isFailure)
    }
}