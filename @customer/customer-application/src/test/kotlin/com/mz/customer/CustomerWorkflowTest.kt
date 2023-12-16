package com.mz.customer

import com.mz.customer.api.domain.ReservationStatus
import com.mz.customer.api.domain.command.RegisterCustomer
import com.mz.customer.api.domain.command.RequestNewCustomerReservation
import com.mz.customer.api.domain.command.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.CustomerApi
import com.mz.ddd.common.api.domain.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest(classes = [CustomerTestConfiguration::class])
class CustomerWorkflowTest {

    @Autowired
    lateinit var customerApi: CustomerApi

    @Test
    fun `register new customer and create the reservation`() {
        val randomString = uuid()
        val registerCustomer = RegisterCustomer(
            lastName = LastName("Doe"),
            firstName = FirstName("John"),
            email = Email("test-${randomString}@test.com")
        )

        val reservationId = uuid()

        val result = customerApi.execute(registerCustomer).block()

        val newCustomerReservation = RequestNewCustomerReservation(
            customerId = result!!.aggregateId,
            reservationId = Id(reservationId)
        )

        StepVerifier.create(customerApi.execute(newCustomerReservation))
            .assertNext { document ->
                document.reservations.any { it.id.value == reservationId && it.status == ReservationStatus.REQUESTED }
            }
            .verifyComplete()

        val updateCustomerReservationAsConfirmed = UpdateCustomerReservationAsConfirmed(
            customerId = result.aggregateId,
            reservationId = Id(reservationId)
        )

        StepVerifier.create(customerApi.execute(updateCustomerReservationAsConfirmed))
            .assertNext { document ->
                document.reservations.any { it.id.value == reservationId && it.status == ReservationStatus.CONFIRMED }
            }
            .verifyComplete()
    }

    @Test
    fun `register new customer and create the reservation, then reservation is declined`() {
        val randomString = uuid()
        val registerCustomer = RegisterCustomer(
            lastName = LastName("Doe"),
            firstName = FirstName("John"),
            email = Email("test-${randomString}@test.com")
        )

        val reservationId = uuid()

        val result = customerApi.execute(registerCustomer).block()

        val newCustomerReservation = RequestNewCustomerReservation(
            customerId = result!!.aggregateId,
            reservationId = Id(reservationId)
        )

        StepVerifier.create(customerApi.execute(newCustomerReservation))
            .assertNext { document ->
                document.reservations.any { it.id.value == reservationId && it.status == ReservationStatus.REQUESTED }
            }
            .verifyComplete()

        val updateCustomerReservationAsDeclined = UpdateCustomerReservationAsConfirmed(
            customerId = result.aggregateId,
            reservationId = Id(reservationId)
        )

        StepVerifier.create(customerApi.execute(updateCustomerReservationAsDeclined))
            .assertNext { document ->
                document.reservations.any { it.id.value == reservationId && it.status == ReservationStatus.DECLINED }
            }
            .verifyComplete()
    }

}