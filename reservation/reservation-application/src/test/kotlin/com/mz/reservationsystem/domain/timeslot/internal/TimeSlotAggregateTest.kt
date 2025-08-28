package com.mz.reservationsystem.application.timeslot.internal

import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.domain.api.timeslot.BookTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotBooked
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotCreated
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotUpdated
import com.mz.reservationsystem.domain.api.timeslot.UpdateTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.toEvent
import com.mz.reservationsystem.application.internal.timeslot.NoneTimeSlotAggregate
import com.mz.reservationsystem.application.internal.timeslot.SomeTimeSlotAggregate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import kotlin.time.Duration

class TimeSlotAggregateTest {

    @Test
    fun `should create time slot`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))
        val noneTimeSlotAggregate = NoneTimeSlotAggregate(newId())
        val createTimeSlot = CreateTimeSlot(
            newId(),
            startTime,
            endTime,
            false
        )

        val events = noneTimeSlotAggregate.verify(createTimeSlot)
        val aggregate = noneTimeSlotAggregate.apply(events.first() as TimeSlotCreated)

        assertThat(aggregate).isInstanceOf(SomeTimeSlotAggregate::class.java)
    }

    @Test
    fun `should not create time slot`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.minus(Duration.parse("PT3H"))
        val noneTimeSlotAggregate = NoneTimeSlotAggregate(newId())

        val createTimeSlot = CreateTimeSlot(
            newId(),
            startTime,
            endTime,
            false
        )
        assertThatThrownBy {
            noneTimeSlotAggregate.verify(createTimeSlot)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should update time slot`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))
        val noneTimeSlotAggregate = NoneTimeSlotAggregate(newId())
        val createTimeSlot = CreateTimeSlot(
            newId(),
            startTime,
            endTime,
            false
        )

        val events = noneTimeSlotAggregate.verify(createTimeSlot)
        val aggregate = noneTimeSlotAggregate.apply(events.first() as TimeSlotCreated)

        val updateStartTime = startTime.plus(Duration.parse("PT3H"))
        val updateEndTime = updateStartTime.plus(Duration.parse("PT3H"))

        val updateTimeSlot = UpdateTimeSlot(
            aggregate.aggregateId,
            updateStartTime,
            updateEndTime,
            true,
            true,
            reservationId = newId()
        )

        val updateEvents = aggregate.verify(updateTimeSlot)
        val updatedAggregate = aggregate.apply(updateEvents.first() as TimeSlotUpdated)

        assertThat(updatedAggregate.version).isEqualTo(aggregate.version.increment())
        assertThat(updatedAggregate.startTime).isEqualTo(updateStartTime)
        assertThat(updatedAggregate.endTime).isEqualTo(updateEndTime)
        assertThat(updatedAggregate.booked).isEqualTo(true)
        assertThat(updatedAggregate.valid).isEqualTo(true)
        assertThat(updateTimeSlot.reservationId).isEqualTo((updateEvents.first() as TimeSlotUpdated).reservationId)
        assertThat(updatedAggregate.reservationId).isEqualTo(updateTimeSlot.reservationId)
    }

    @Test
    fun `should update time slot as booked when missing reservation id`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))
        val noneTimeSlotAggregate = NoneTimeSlotAggregate(newId())
        val createTimeSlot = CreateTimeSlot(
            newId(),
            startTime,
            endTime,
            false
        )

        val events = noneTimeSlotAggregate.verify(createTimeSlot)
        val aggregate = noneTimeSlotAggregate.apply(events.first() as TimeSlotCreated)

        val updateStartTime = startTime.plus(Duration.parse("PT3H"))
        val updateEndTime = updateStartTime.plus(Duration.parse("PT3H"))

        val updateTimeSlot = UpdateTimeSlot(
            aggregate.aggregateId,
            updateStartTime,
            updateEndTime,
            true,
            true
        )

        assertThatThrownBy {
            aggregate.verify(updateTimeSlot)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should not update time slot`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))
        val noneTimeSlotAggregate = NoneTimeSlotAggregate(newId())
        val createTimeSlot = CreateTimeSlot(
            newId(),
            startTime,
            endTime,
            false
        )

        val events = noneTimeSlotAggregate.verify(createTimeSlot)
        val aggregate = noneTimeSlotAggregate.apply(events.first() as TimeSlotCreated)

        val updateStartTime = startTime.plus(Duration.parse("PT5H"))
        val updateEndTime = updateStartTime.minus(Duration.parse("PT3H"))

        val updateTimeSlot = UpdateTimeSlot(
            aggregate.aggregateId,
            updateStartTime,
            updateEndTime,
            true,
            true,
            reservationId = newId()
        )
        assertThatThrownBy {
            aggregate.verify(updateTimeSlot)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should book time slot`() {
        val startTime = instantNow().plus(Duration.parse("PT2H"))
        val endTime = startTime.plus(Duration.parse("PT3H"))
        val noneTimeSlotAggregate = NoneTimeSlotAggregate(newId())
        val createTimeSlot = CreateTimeSlot(
            newId(),
            startTime,
            endTime,
            false
        )

        val aggregate = noneTimeSlotAggregate.apply(createTimeSlot.toEvent(noneTimeSlotAggregate.aggregateId))

        val bookTimeSlot = BookTimeSlot(
            aggregate.aggregateId,
            newId(),
            true
        )

        val bookEvents = aggregate.verify(bookTimeSlot)
        val bookedAggregate = aggregate.apply(bookEvents.first() as TimeSlotBooked)

        assertThat(bookedAggregate.version).isEqualTo(aggregate.version.increment())
        assertThat(bookedAggregate.booked).isEqualTo(true)
        assertThat(bookedAggregate.valid).isEqualTo(true)
        assertThat(bookTimeSlot.reservationId).isEqualTo((bookEvents.first() as TimeSlotBooked).reservationId)
        assertThat(bookedAggregate.reservationId).isEqualTo(bookTimeSlot.reservationId)
    }

}
