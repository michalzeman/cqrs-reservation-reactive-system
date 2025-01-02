package com.mz.reservationsystem.workflow.tests

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.json.registerRequiredModules
import com.mz.customer.adapter.rest.api.model.NewCustomerReservationRequest
import com.mz.customer.adapter.rest.api.model.RegisterCustomerRequest
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.ReservationStatus
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import com.mz.reservationsystem.adapter.model.timeslot.CreateTimeSlotRequest
import org.junit.jupiter.api.Tag
import kotlin.random.Random
import kotlin.time.Duration


private const val RESERVATION_SERVICE_URL = "http://localhost:8081"

private const val CUSTOMER_SERVICE_URL = "http://localhost:8082"

@Tag("systemChecks")
class ReservationLifecycleSystemChecksTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Flux.concat(
                waitForServiceToStart(RESERVATION_SERVICE_URL),
                waitForServiceToStart(CUSTOMER_SERVICE_URL)
            ).collectList()
                .block()
        }
    }

    private val webClientBuilder: WebTestClient.Builder = webClient()

    private val reservationServiceClient = webClientBuilder.baseUrl(RESERVATION_SERVICE_URL).build()

    private val customerServiceClient = webClientBuilder.baseUrl(CUSTOMER_SERVICE_URL).build()

    @Test
    fun `run reservation work flow test`() {
        val randomHours = Random.nextLong(1, 100)
        val startTime = instantNow().plus(Duration.parse("PT${randomHours}H"))
        val endTime = startTime.plus(Duration.parse("PT1H"))

        prepareTimeSlot(startTime, endTime)

        val randomString = uuid()
        val registerCustomerReservation = RegisterCustomerRequest(
            lastName = "Doe",
            firstName = "John",
            email = "test-${randomString}@test.com"
        )

        val responseCustomer = prepareCustomer(registerCustomerReservation)

        val newCustomerReservation = NewCustomerReservationRequest(
            customerId = responseCustomer.aggregateId.value,
            requestId = uuid(),
            startTime = startTime,
            endTime = endTime
        )

        customerServiceClient.put()
            .uri("/customers/${newCustomerReservation.customerId}/reservations")
            .contentType(APPLICATION_JSON)
            .body(newCustomerReservation.toMono(), NewCustomerReservationRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(CustomerDocument::class.java)
            .returnResult()
            .responseBody!!

        Thread.sleep(5000)

        val customerDocument = customerServiceClient.get().uri("/customers/${responseCustomer.aggregateId.value}")
            .exchange()
            .expectBody<CustomerDocument>()
            .returnResult()
            .responseBody!!

        Assertions.assertThat(customerDocument.reservations.any { it.status == ReservationStatus.CONFIRMED }).isTrue()
    }

    private fun prepareCustomer(registerCustomerReservation: RegisterCustomerRequest): CustomerDocument {
        return customerServiceClient.post()
            .uri("/customers")
            .contentType(APPLICATION_JSON)
            .body(Mono.just(registerCustomerReservation), RegisterCustomerRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(CustomerDocument::class.java)
            .returnResult()
            .responseBody!!
    }

    private fun prepareTimeSlot(
        startTime: Instant,
        endTime: Instant
    ): TimeSlotDocument {
        val createTimeSlotRequest = CreateTimeSlotRequest(
            startTime.toJavaInstant(),
            endTime.toJavaInstant()
        )

        // Create the time slot
        return reservationServiceClient.post()
            .uri("/reservation-system/time-slots")
            .contentType(APPLICATION_JSON)
            .body(Mono.just(createTimeSlotRequest), CreateTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(TimeSlotDocument::class.java)
            .returnResult()
            .responseBody!!


    }

    private fun webClient(): WebTestClient.Builder {
        val objectMapper = jacksonObjectMapper().registerRequiredModules()
        val strategies = ExchangeStrategies
            .builder()
            .codecs { configurer ->
                configurer.defaultCodecs()
                    .jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, APPLICATION_JSON))
                configurer.defaultCodecs()
                    .jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, APPLICATION_JSON))
            }.build()

        return WebTestClient.bindToServer()
            .exchangeStrategies(strategies)
    }
}

private fun waitForServiceToStart(url: String): Mono<String> {
    val healthEndpoint = "/actuator/health"

    val webClient = WebClient.builder().baseUrl(url).build()

    return webClient.get().uri(healthEndpoint)
        .retrieve()
        .bodyToMono(String::class.java)
        .map { responseBody ->
            val jsonNode = ObjectMapper().readTree(responseBody)
            jsonNode.get("status").asText()
        }
        .filter { status -> status == "UP" }
        .retryWhen(Retry.backoff(5, java.time.Duration.ofSeconds(2)))
        .repeatWhenEmpty(30) { it.delayElements(java.time.Duration.ofSeconds(2)) }
}