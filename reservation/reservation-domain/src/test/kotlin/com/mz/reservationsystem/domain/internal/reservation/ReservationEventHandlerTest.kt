package com.mz.reservationsystem.domain.internal.reservation

import com.mz.reservationsystem.domain.api.reservation.ReservationRequested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ReservationEventHandlerTest {

    private val cut = ReservationEventHandler()

    @Test
    fun apply() {
        val event = mock<ReservationRequested>()
        val aggregate = mock<NoneReservationAggregate>()
        cut.apply(aggregate, event)
        verify(aggregate).applyEvent(event)
    }
}