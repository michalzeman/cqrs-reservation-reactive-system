package com.mz.reservationsystem.adapter.rest.timeslot

import com.mz.reservationsystem.application.timeslot.FindTimeSlotBetweenTimes
import com.mz.reservationsystem.application.timeslot.FindTimeSlotByBooked
import com.mz.reservationsystem.application.timeslot.FindTimeSlotsByConditions
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.*

private const val START_TIME = "start_time"

private const val END_TIME = "end_time"

private const val BOOKED = "booked"

class TimeSlotHttpHandlerKtTest {

    @Test
    fun `isBoolean, when empty string, then false`() {
        assertThat("".isBoolean()).isFalse()
    }

    @Test
    fun `isBoolean, when number, then false`() {
        assertThat("1".isBoolean()).isFalse()
    }

    @Test
    fun `isBoolean, when true, then true`() {
        assertThat("true".isBoolean()).isTrue()
    }

    @Test
    fun `mapToQuery, when startTime endTime booked, then FindTimeSlotsByConditions`() {
        val serverRequest = mock<ServerRequest>()

        whenever(serverRequest.queryParam(START_TIME)) doReturn Optional.of("2022-01-01T00:00:00Z")
        whenever(serverRequest.queryParam(END_TIME)) doReturn Optional.of("2022-01-01T23:59:59Z")
        whenever(serverRequest.queryParam(BOOKED)) doReturn Optional.of("false")

        val result = serverRequest.mapToQuery()

        assertThat(result).isEqualTo(
            FindTimeSlotsByConditions(
                setOf(
                    FindTimeSlotBetweenTimes(
                        startTime = Instant.parse("2022-01-01T00:00:00Z"),
                        endTime = Instant.parse("2022-01-01T23:59:59Z")
                    ),
                    FindTimeSlotByBooked(false)
                )
            )
        )
    }

    @Test
    fun `mapToQuery, when startTime endTime, then FindTimeSlotBetweenTimes`() {
        val serverRequest = mock<ServerRequest>()

        whenever(serverRequest.queryParam(START_TIME)) doReturn Optional.of("2022-01-01T00:00:00Z")
        whenever(serverRequest.queryParam(END_TIME)) doReturn Optional.of("2022-01-01T23:59:59Z")
        whenever(serverRequest.queryParam(BOOKED)) doReturn Optional.empty()

        val result = serverRequest.mapToQuery()

        assertThat(result).isEqualTo(
            FindTimeSlotBetweenTimes(
                startTime = Instant.parse("2022-01-01T00:00:00Z"),
                endTime = Instant.parse("2022-01-01T23:59:59Z")
            )
        )
    }

    @Test
    fun `mapToQuery, when only startTime, then null`() {
        val serverRequest = mock<ServerRequest>()

        whenever(serverRequest.queryParam("startTime")) doReturn Optional.of("2022-01-01T00:00:00Z")
        whenever(serverRequest.queryParam("endTime")) doReturn Optional.empty()
        whenever(serverRequest.queryParam(BOOKED)) doReturn Optional.empty()

        val result = serverRequest.mapToQuery()

        assertThat(result).isNull()
    }

    @Test
    fun `mapToQuery, when only endTime, then null`() {
        val serverRequest = mock<ServerRequest>()

        whenever(serverRequest.queryParam("startTime")) doReturn Optional.empty()
        whenever(serverRequest.queryParam("endTime")) doReturn Optional.of("2022-01-01T23:59:59Z")
        whenever(serverRequest.queryParam(BOOKED)) doReturn Optional.empty()

        val result = serverRequest.mapToQuery()

        assertThat(result).isNull()
    }

    @Test
    fun `mapToQuery, when no params, then null`() {
        val serverRequest = mock<ServerRequest>()

        whenever(serverRequest.queryParam("startTime")) doReturn Optional.empty()
        whenever(serverRequest.queryParam("endTime")) doReturn Optional.empty()
        whenever(serverRequest.queryParam(BOOKED)) doReturn Optional.empty()

        val result = serverRequest.mapToQuery()

        assertThat(result).isNull()
    }

}