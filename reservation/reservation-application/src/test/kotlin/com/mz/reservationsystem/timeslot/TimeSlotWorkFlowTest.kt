package com.mz.reservationsystem.timeslot

import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.TestReservationSystemConfiguration
import com.mz.reservationsystem.adapter.model.timeslot.BookTimeSlotRequest
import com.mz.reservationsystem.adapter.model.timeslot.CreateTimeSlotRequest
import com.mz.reservationsystem.adapter.model.timeslot.UpdateTimeSlotRequest
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.datetime.toJavaInstant
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.cassandra.core.cql.CqlTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import kotlin.time.Duration

@SpringBootTest(
    classes = [TestReservationSystemConfiguration::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class TimeSlotWorkFlowTest(@LocalServerPort val port: Int) {

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
    fun `should create, update and book time slot`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))
        val createTimeSlotRequest = CreateTimeSlotRequest(
            startTime.toJavaInstant(),
            endTime.toJavaInstant()
        )

        // Create the time slot
        val response = client.post()
            .uri("/reservation-system/time-slots")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createTimeSlotRequest), CreateTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(TimeSlotDocument::class.java)
            .returnResult()
            .responseBody

        val aggregateId = response?.aggregateId?.value ?: ""

        val updateStartTime = startTime.plus(Duration.parse("PT4H"))
        val updateEndTime = updateStartTime.plus(Duration.parse("PT4H"))
        // Update the time slot
        val updateTimeSlotRequest = UpdateTimeSlotRequest(
            aggregateId,
            updateStartTime.toJavaInstant(),
            updateEndTime.toJavaInstant(),
            valid = true,
        )

        client.put()
            .uri("/reservation-system/time-slots")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(updateTimeSlotRequest), UpdateTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(TimeSlotDocument::class.java)
        // Book the time slot
        val bookTimeSlotRequest = BookTimeSlotRequest(aggregateId, true, uuid())

        client.put()
            .uri("/reservation-system/time-slots/book")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(bookTimeSlotRequest), BookTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isAccepted
            .expectBody(TimeSlotDocument::class.java)

        val returnResult = client.get()
            .uri(
                "/reservation-system/time-slots" +
                        "?start_time=${updateStartTime}&end_time=${updateEndTime}&booked=true"
            )
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<List<TimeSlotDocument>>()
            .returnResult().responseBody

        Assertions.assertThat(returnResult?.size).isEqualTo(1)
    }

    @Test
    fun `should not create time slot if already exists`() {
        val startTime = instantNow().plus(Duration.parse("PT4H"))
        val endTime = startTime.plus(Duration.parse("PT5H"))
        val createTimeSlotRequest = CreateTimeSlotRequest(
            startTime.toJavaInstant(),
            endTime.toJavaInstant()
        )

        // Create the time slot
        client.post()
            .uri("/reservation-system/time-slots")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createTimeSlotRequest), CreateTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isAccepted

        // Try to create the same time slot again
        client.post()
            .uri("/reservation-system/time-slots")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createTimeSlotRequest), CreateTimeSlotRequest::class.java)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.PRECONDITION_FAILED)
    }
}