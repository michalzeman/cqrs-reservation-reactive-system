package com.mz.reservationsystem.application.internal.reservation

import com.mz.reservationsystem.domain.api.reservation.RequestReservation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ReservationCommandHandlerTest {

    private val cut = ReservationCommandHandler()

    @Test
    fun execute() {
        val cmd = mock<RequestReservation>()
        val aggregate = mock<NoneReservationAggregate>()
        assertThat(cut.execute(aggregate, cmd).isSuccess).isTrue()
        verify(aggregate).verifyCommand(cmd)
    }
}