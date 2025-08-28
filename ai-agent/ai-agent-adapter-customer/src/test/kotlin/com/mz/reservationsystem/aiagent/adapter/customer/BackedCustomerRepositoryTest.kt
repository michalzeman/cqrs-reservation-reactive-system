package com.mz.reservationsystem.aiagent.adapter.customer

import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.LastName
import com.mz.reservationsystem.aiagent.application.customer.RegisterCustomer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class BackedCustomerRepositoryTest {

    @Test
    fun `map RegisterCustomer to Request`() {
        val customerData = RegisterCustomer(
            lastName = LastName("Doe"),
            firstName = FirstName("John"),
            email = Email("test@test.com")
        )

        val registerCustomerRequest = customerData.map()

        assertThat(registerCustomerRequest.email).isEqualTo(customerData.email.value)
        assertThat(registerCustomerRequest.firstName).isEqualTo(customerData.firstName.value)
        assertThat(registerCustomerRequest.lastName).isEqualTo(customerData.lastName.value)
    }
}