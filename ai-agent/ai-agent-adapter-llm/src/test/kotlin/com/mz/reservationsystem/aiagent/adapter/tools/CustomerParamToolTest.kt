package com.mz.reservationsystem.aiagent.adapter.tools

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerParam
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.toRegisterCustomer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomerParamToolTest {

    @Test
    fun toRegisterCustomer() {
        val customerParam = CustomerParam("Michal", "Zeman", "test@test.org")

        val actual = customerParam.toRegisterCustomer()

        assertThat(actual.email.value).isEqualTo(customerParam.email)
        assertThat(actual.firstName.value).isEqualTo(customerParam.firstName)
        assertThat(actual.lastName.value).isEqualTo(customerParam.lastName)
    }
}