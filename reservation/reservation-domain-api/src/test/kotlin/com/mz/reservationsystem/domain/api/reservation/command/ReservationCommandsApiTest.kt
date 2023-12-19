package com.mz.reservationsystem.domain.api.reservation.command

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.reservationsystem.domain.api.reservation.DeclineReservation
import com.mz.reservationsystem.domain.api.reservation.RequestReservation
import com.mz.reservationsystem.domain.api.reservation.toDomainEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration

class ReservationCommandsApiTest {

    @Test
    fun `RequestReservation to ReservationRequested`() {
        val command = RequestReservation(
            aggregateId = Id("1"),
            customerId = Id("2"),
            requestId = Id("3"),
            startTime = instantNow(),
            endTime = instantNow().plus(duration = Duration.parse("PT1H")),
        )
        val event = command.toDomainEvent()
        assertEquals("1", event.aggregateId.value)
        assertEquals("2", event.customerId.value)
        assertEquals("3", event.requestId.value)
        assertEquals(command.endTime, event.endTime)
        assertEquals(command.startTime, event.startTime)
        assertEquals(event.correlationId, command.correlationId)
    }

    @Test
    fun `DeclineReservation to ReservationDeclined`() {
        val command = DeclineReservation(
            aggregateId = Id("1"),
            customerId = Id("2"),
            requestId = Id("3")
        )
        val event = command.toDomainEvent()
        assertEquals("1", event.aggregateId.value)
        assertEquals("2", event.customerId.value)
        assertEquals("3", event.requestId.value)
        assertEquals(event.correlationId, command.correlationId)
    }
}