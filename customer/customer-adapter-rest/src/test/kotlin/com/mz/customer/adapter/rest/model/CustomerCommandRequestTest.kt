package com.mz.customer.adapter.rest.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mz.common.components.json.registerRequiredModules
import com.mz.customer.adapter.rest.api.model.CustomerCommandRequest
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.domain.api.RequestNewCustomerReservation
import com.mz.customer.domain.api.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.api.UpdateCustomerReservationAsDeclined
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomerCommandRequestTest {

    val objectMapper = jacksonObjectMapper().registerRequiredModules()

    @Test
    fun `RegisterCustomer command mapped correctly`() {
        val jsonTest = """
            {
                "type":"register-customer",
                "firstName":"John",
                "lastName":"Doe",
                "email":"john.doe@example.com",
                "correlationId":"123",
                "createdAt":"2021-03-01T00:00:00.000Z",
                "commandId":"456"
            }
            """.trimIndent()
        val request = objectMapper.readValue<CustomerCommandRequest>(jsonTest).toCommand()
        val cmd = request as RegisterCustomer
        assertThat(cmd.firstName.value).isEqualTo("John")
        assertThat(cmd.lastName.value).isEqualTo("Doe")
        assertThat(cmd.createdAt).isEqualTo(Instant.parse("2021-03-01T00:00:00.000Z"))
        assertThat(cmd.correlationId.value).isEqualTo("123")
        assertThat(cmd.commandId.value).isEqualTo("456")
    }

    @Test
    fun `NewCustomerReservationRequest command mapped correctly`() {
        val jsonTest = """
            {
                "type":"new-customer-reservation",
                "customerId":"123",
                "reservationId":"456",
                "startTime":"2021-03-01T00:00:00.000Z",
                "endTime":"2021-03-01T01:00:00.000Z",
                "correlationId":"789",
                "createdAt":"2021-03-01T00:00:00.000Z",
                "commandId":"012"
            }
            """.trimIndent()
        val request = objectMapper.readValue<CustomerCommandRequest>(jsonTest).toCommand()
        val cmd = request as RequestNewCustomerReservation
        assertThat(cmd.customerId.value).isEqualTo("123")
        assertThat(cmd.reservationId.value).isEqualTo("456")
        assertThat(cmd.createdAt).isEqualTo(Instant.parse("2021-03-01T00:00:00.000Z"))
        assertThat(cmd.correlationId.value).isEqualTo("789")
        assertThat(cmd.commandId.value).isEqualTo("012")
    }

    @Test
    fun `UpdateCustomerReservationAsConfirmedRequest command mapped correctly`() {
        val jsonTest = """
            {
                "type":"update-customer-reservation-as-confirmed",
                "customerId":"123",
                "requestId":"012",
                "reservationId":"456",
                "correlationId":"789",
                "createdAt":"2021-03-01T00:00:00.000Z",
                "commandId":"012"
            }
            """.trimIndent()
        val request = objectMapper.readValue<CustomerCommandRequest>(jsonTest).toCommand()
        val command = request as UpdateCustomerReservationAsConfirmed
        assertThat(command.customerId.value).isEqualTo("123")
        assertThat(command.reservationId.value).isEqualTo("456")
        assertThat(command.createdAt).isEqualTo(Instant.parse("2021-03-01T00:00:00.000Z"))
        assertThat(command.correlationId.value).isEqualTo("789")
        assertThat(command.commandId.value).isEqualTo("012")
    }

    @Test
    fun `UpdateCustomerReservationAsDeclinedRequest command mapped correctly`() {
        val jsonTest = """
            {
                "type":"update-customer-reservation-as-declined",
                "customerId":"123",
                "requestId":"012",
                "reservationId":"456",
                "correlationId":"789",
                "createdAt":"2021-03-01T00:00:00.000Z",
                "commandId":"012"
            }
            """.trimIndent()

        val request = objectMapper.readValue<CustomerCommandRequest>(jsonTest).toCommand()
        val command = request as UpdateCustomerReservationAsDeclined
        assertThat(command.customerId.value).isEqualTo("123")
        assertThat(command.reservationId.value).isEqualTo("456")
        assertThat(command.createdAt).isEqualTo(Instant.parse("2021-03-01T00:00:00.000Z"))
        assertThat(command.correlationId.value).isEqualTo("789")
        assertThat(command.commandId.value).isEqualTo("012")
    }
}
