package com.mz.reservationsystem.domain.timeslot.internal

import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCreated
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class TimeSlotEventHandlerTest {

    private val cut = TimeSlotEventHandler()

    @Test
    @Disabled
    fun apply() {
        val event = mock<TimeSlotCreated>()
        val aggregate = mock<NoneTimeSlotAggregate>()
        cut.apply(aggregate, event)
        verify(aggregate).apply(event)
    }
}
