package com.mz.customer

import com.mz.customer.domain.CustomerApi
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.domain.api.RequestNewCustomerReservation
import com.mz.customer.domain.api.ReservationPeriod
import com.mz.customer.domain.api.ReservationStatus
import com.mz.customer.domain.api.UpdateCustomerReservationAsConfirmed
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import com.mz.ddd.common.api.domain.uuid
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@Tag("systemChecks")
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

        val requestId = uuid()

        val result = customerApi.execute(registerCustomer).block()

        val newCustomerReservation = RequestNewCustomerReservation(
            customerId = result!!.aggregateId,
            requestId = Id(requestId),
            ReservationPeriod(instantNow(), instantNow())
        )

        StepVerifier.create(customerApi.execute(newCustomerReservation))
            .assertNext { document ->
                document.reservations.any { it.id?.value == requestId && it.status == ReservationStatus.REQUESTED }
            }
            .verifyComplete()

        val updateCustomerReservationAsConfirmed = UpdateCustomerReservationAsConfirmed(
            customerId = result.aggregateId,
            requestId = Id(requestId),
            reservationId = newId()
        )

        StepVerifier.create(customerApi.execute(updateCustomerReservationAsConfirmed))
            .assertNext { document ->
                document.reservations.any { it.id?.value == requestId && it.status == ReservationStatus.CONFIRMED }
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

        val requestId = uuid()

        val result = customerApi.execute(registerCustomer).block()

        val newCustomerReservation = RequestNewCustomerReservation(
            customerId = result!!.aggregateId,
            requestId = Id(requestId),
            ReservationPeriod(instantNow(), instantNow())
        )

        StepVerifier.create(customerApi.execute(newCustomerReservation))
            .assertNext { document ->
                document.reservations.any { it.id?.value == requestId && it.status == ReservationStatus.REQUESTED }
            }
            .verifyComplete()

        val updateCustomerReservationAsDeclined = UpdateCustomerReservationAsConfirmed(
            customerId = result.aggregateId,
            requestId = Id(requestId),
            reservationId = newId()
        )

        StepVerifier.create(customerApi.execute(updateCustomerReservationAsDeclined))
            .assertNext { document ->
                document.reservations.any { it.id?.value == requestId && it.status == ReservationStatus.DECLINED }
            }
            .verifyComplete()
    }

}