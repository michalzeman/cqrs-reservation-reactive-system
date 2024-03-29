package com.mz.reservationsystem.domain.timeslot.internal

import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCreated
import com.mz.reservationsystem.domain.internal.timeslot.NoneTimeSlotAggregate
import com.mz.reservationsystem.domain.internal.timeslot.TimeSlotEventHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class TimeSlotEventHandlerTest {

    private val cut = TimeSlotEventHandler()

    @Test
    fun apply() {
        val event = mock<TimeSlotCreated>()
        val aggregate = mock<NoneTimeSlotAggregate>()
        cut.apply(aggregate, event)
        verify(aggregate).apply(event)
    }
}
