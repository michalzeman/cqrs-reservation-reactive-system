package com.mz.reservationsystem.reservation

import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.TestReservationSystemConfiguration
import com.mz.reservationsystem.adapter.rest.reservation.model.DeclineReservationRequest
import com.mz.reservationsystem.adapter.rest.reservation.model.RequestReservationRequest
import com.mz.reservationsystem.adapter.rest.timeslot.model.CreateTimeSlotRequest
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import com.mz.reservationsystem.domain.api.reservation.ReservationState
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.datetime.toJavaInstant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.cassandra.core.cql.CqlTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import kotlin.time.Duration

@SpringBootTest(
    classes = [TestReservationSystemConfiguration::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ReservationWorkFlowTest(@LocalServerPort val port: Int) {

    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

    @Autowired
    internal lateinit var cqlTemplate: CqlTemplate


    @BeforeEach
    fun setUp() {
        val cqlEventJournal = "TRUNCATE event_journal"
        val cqlSnapshot = "TRUNCATE event_journal"
        val deleteQueryableInstant = "TRUNCATE queryable_timestamp"
        val deleteQueryableBoolean = "TRUNCATE queryable_boolean"

        cqlTemplate.execute(cqlEventJournal)
        cqlTemplate.execute(cqlSnapshot)
        cqlTemplate.execute(deleteQueryableInstant)
        cqlTemplate.execute(deleteQueryableBoolean)
    }

    @Test
    fun `should request, accept, decline and verify reservation`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))

        val createTimeSlotRequest = CreateTimeSlotRequest(
            startTime.toJavaInstant(),
            endTime.toJavaInstant()
        )

        // Create the time slot
        val responseTimeSlot = client.post()
            .uri("/reservation-system/time-slots")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createTimeSlotRequest), CreateTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(TimeSlotDocument::class.java)
            .returnResult()
            .responseBody

        val requestReservationRequest = RequestReservationRequest(
            customerId = uuid(),
            requestId = uuid(),
            startTime = startTime.toJavaInstant(),
            endTime = endTime.toJavaInstant()
        )

        // Request the reservation
        val response = client.post()
            .uri("/reservation-system/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(requestReservationRequest), RequestReservationRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(ReservationDocument::class.java)
            .returnResult()
            .responseBody!!

        assertThat(response.reservationState).isEqualTo(ReservationState.REQUESTED)

        val aggregateId = response.aggregateId.value

        // wait to accept the reservation
        Thread.sleep(2000)

        // Verify the reservation by calling it by id
        val acceptedReservation = client.get()
            .uri("/reservation-system/reservations/$aggregateId")
            .exchange()
            .expectStatus().isAccepted
            .expectBody(ReservationDocument::class.java)
            .returnResult()
            .responseBody!!

        assertThat(acceptedReservation.reservationState).isEqualTo(ReservationState.ACCEPTED)
        assertThat(acceptedReservation.version.value).isEqualTo(1)

        // Decline the reservation
        val declineReservationRequest = DeclineReservationRequest(
            aggregateId = aggregateId
        )
        var declinedReservation = client.put()
            .uri("/reservation-system/reservations/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(declineReservationRequest), DeclineReservationRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(ReservationDocument::class.java)
            .returnResult()
            .responseBody!!

        assertThat(declinedReservation.reservationState).isEqualTo(ReservationState.DECLINED)
    }
}