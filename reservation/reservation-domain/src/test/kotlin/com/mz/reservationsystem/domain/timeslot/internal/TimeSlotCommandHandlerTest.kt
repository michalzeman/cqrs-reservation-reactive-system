package com.mz.reservationsystem.domain.timeslot.internal

import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class TimeSlotCommandHandlerTest {
    private val cut = TimeSlotCommandHandler()

    @Test
    fun `Execute create time slot`() {
        val cmd = mock<CreateTimeSlot>()
        val aggregate = mock<NoneTimeSlotAggregate>()
        Assertions.assertThat(cut.execute(aggregate, cmd).isSuccess)
        verify(aggregate).verify(cmd)
    }
}
