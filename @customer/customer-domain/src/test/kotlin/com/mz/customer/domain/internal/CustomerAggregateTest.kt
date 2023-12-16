package com.mz.customer.domain.internal

import com.mz.customer.domain.api.Reservation
import com.mz.customer.domain.api.ReservationStatus
import com.mz.customer.domain.api.command.RegisterCustomer
import com.mz.customer.domain.api.command.RequestNewCustomerReservation
import com.mz.customer.domain.api.command.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.api.command.UpdateCustomerReservationAsDeclined
import com.mz.customer.domain.api.event.CustomerRegistered
import com.mz.customer.domain.api.event.CustomerReservationConfirmed
import com.mz.customer.domain.api.event.CustomerReservationDeclined
import com.mz.customer.domain.api.event.CustomerReservationRequested
import com.mz.ddd.common.api.domain.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CustomerAggregateTest {

    @Test
    fun `registering a customer should produce a CustomerRegistered event`() {
        val emptyCustomer = EmptyCustomer(Id("1"))
        val registerCustomerCommand =
            RegisterCustomer(LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val events = emptyCustomer.verifyRegisterCustomer(registerCustomerCommand)
        assertTrue(events.single() is CustomerRegistered)
    }

    @Test
    fun `requesting a new reservation should produce a CustomerReservationRequested event`() {
        val existingCustomer =
            ExistingCustomer(Id("1"), Version(1), LastName("Doe"), FirstName("John"), Email("john.doe@example.com"))
        val requestNewCustomerReservationCommand = RequestNewCustomerReservation(Id("1"), Id("reservation:2"))
        val events = existingCustomer.verifyRequestNewCustomerReservation(requestNewCustomerReservationCommand)
        assertTrue(events.single() is CustomerReservationRequested)
    }

    @Test
    fun `requesting a new reservation with an existing reservation id should throw an exception`() {
        val existingReservation = Reservation(Id("reservation:2"), ReservationStatus.REQUESTED)
        val existingCustomer = ExistingCustomer(
            Id("1"),
            Version(2),
            LastName("Doe"),
            FirstName("John"),
            Email("john.doe@example.com"),
            setOf(existingReservation)
        )
        val requestNewCustomerReservationCommand = RequestNewCustomerReservation(Id("1"), Id("reservation:2"))
        assertThrows<RuntimeException> {
            existingCustomer.verifyRequestNewCustomerReservation(requestNewCustomerReservationCommand)
        }
    }

    @Test
    fun `confirming a reservation should produce a CustomerReservationConfirmed event`() {
        val existingReservation = Reservation(Id("reservation:2"), ReservationStatus.REQUESTED)
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(1),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
                setOf(existingReservation)
            )
        val updateCustomerReservationAsConfirmedCommand =
            UpdateCustomerReservationAsConfirmed(Id("1"), newId(), Id("reservation:2"))
        val events =
            existingCustomer.verifyUpdateCustomerReservationAsConfirmed(updateCustomerReservationAsConfirmedCommand)
        assertTrue(events.single() is CustomerReservationConfirmed)
    }

    @Test
    fun `confirming non existing reservation should produce a error`() {
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(1),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
            )
        val updateCustomerReservationAsConfirmedCommand =
            UpdateCustomerReservationAsConfirmed(Id("1"), newId(), Id("reservation:2"))

        assertThrows<RuntimeException> {
            existingCustomer.verifyUpdateCustomerReservationAsConfirmed(updateCustomerReservationAsConfirmedCommand)
        }
    }

    @Test
    fun `declining a reservation should produce a CustomerReservationDeclined event`() {
        val existingReservation = Reservation(Id("reservation:2"), ReservationStatus.REQUESTED)
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(2),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
                setOf(existingReservation)
            )
        val updateCustomerReservationAsDeclinedCommand =
            UpdateCustomerReservationAsDeclined(Id("1"), Id("reservation:2"), newId())
        val events =
            existingCustomer.verifyUpdateCustomerReservationAsDeclined(updateCustomerReservationAsDeclinedCommand)
        assertTrue(events.single() is CustomerReservationDeclined)
    }

    @Test
    fun `declining non reservation should produce a error`() {
        val existingCustomer =
            ExistingCustomer(
                Id("1"),
                Version(2),
                LastName("Doe"),
                FirstName("John"),
                Email("john.doe@example.com"),
            )
        val updateCustomerReservationAsDeclinedCommand =
            UpdateCustomerReservationAsDeclined(Id("1"), Id("reservation:2"), newId())
        assertThrows<RuntimeException> {
            existingCustomer.verifyUpdateCustomerReservationAsDeclined(updateCustomerReservationAsDeclinedCommand)
        }
    }
}