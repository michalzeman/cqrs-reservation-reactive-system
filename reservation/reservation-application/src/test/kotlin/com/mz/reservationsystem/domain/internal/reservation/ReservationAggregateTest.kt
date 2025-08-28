package com.mz.reservationsystem.application.internal.reservation

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.reservationsystem.domain.api.reservation.AcceptReservation
import com.mz.reservationsystem.domain.api.reservation.DeclineReservation
import com.mz.reservationsystem.domain.api.reservation.RequestReservation
import com.mz.reservationsystem.domain.api.reservation.ReservationAccepted
import com.mz.reservationsystem.domain.api.reservation.ReservationDeclined
import com.mz.reservationsystem.domain.api.reservation.ReservationRequested
import com.mz.reservationsystem.domain.api.reservation.ReservationState
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ReservationAggregateTest {

    @Test
    fun `NoneReservationAggregate verifyCommand and applyEvent test`() {
        val aggregate = NoneReservationAggregate(Id("test"))
        val requestReservation =
            RequestReservation(Id("test"), Id("customer"), Id("request"), Instant.DISTANT_PAST, Instant.DISTANT_FUTURE)

        // Verify command
        val events = aggregate.verifyCommand(requestReservation)
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(ReservationRequested::class.java)

        // Apply event
        val newAggregate = aggregate.applyEvent(events[0])
        assertThat(newAggregate).isInstanceOf(ReservationRequestedAggregate::class.java)
        assertThat((newAggregate as ReservationRequestedAggregate).reservationState).isEqualTo(ReservationState.REQUESTED)
    }

    @Test
    fun `ReservationRequestedAggregate verifyCommand and applyEvent test`() {
        val aggregate = ReservationRequestedAggregate(
            Id("test"), Id("customer"), Id("request"), Version(), Instant.DISTANT_PAST, Instant.DISTANT_FUTURE
        )
        val acceptReservation = AcceptReservation(Id("test"), Id("timeSlot"))

        // Verify command
        val events = aggregate.verifyCommand(acceptReservation)
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(ReservationAccepted::class.java)

        // Apply event
        val newAggregate = aggregate.applyEvent(events[0])
        assertThat(newAggregate).isInstanceOf(ReservationAcceptedAggregate::class.java)
        assertThat((newAggregate as ReservationAcceptedAggregate).reservationState).isEqualTo(ReservationState.ACCEPTED)
    }

    @Test
    fun `ReservationDeclinedAggregate verifyCommand and applyEvent test`() {
        val aggregate = ReservationDeclinedAggregate(
            Id("test"), Id("customer"), Id("request"), Version(), Instant.DISTANT_PAST, Instant.DISTANT_FUTURE
        )

        // Verify command
        assertThatThrownBy {
            aggregate.verifyCommand(
                RequestReservation(
                    Id("test"), Id("customer"), Id("request"), Instant.DISTANT_PAST, Instant.DISTANT_FUTURE
                )
            )
        }.isInstanceOf(IllegalStateException::class.java).hasMessage("Reservation is declined")

        // Apply event
        assertThatThrownBy {
            aggregate.applyEvent(
                ReservationRequested(
                    Id("test"), Id("customer"), Id("request"), Instant.DISTANT_PAST, Instant.DISTANT_FUTURE
                )
            )
        }.isInstanceOf(IllegalStateException::class.java).hasMessage("Reservation is declined")
    }

    @Test
    fun `ReservationAcceptedAggregate verifyCommand and applyEvent test`() {
        val aggregate = ReservationAcceptedAggregate(
            Id("test"),
            Id("customer"),
            Id("request"),
            Version(),
            Instant.DISTANT_PAST,
            Instant.DISTANT_FUTURE,
            Id("timeSlot")
        )
        val declineReservation = DeclineReservation(Id("test"))

        // Verify command
        val events = aggregate.verifyCommand(declineReservation)
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(ReservationDeclined::class.java)

        // Apply event
        val newAggregate = aggregate.applyEvent(events[0])
        assertThat(newAggregate).isInstanceOf(ReservationDeclinedAggregate::class.java)
        assertThat((newAggregate as ReservationDeclinedAggregate).reservationState).isEqualTo(ReservationState.DECLINED)
    }

    @Test
    fun `ReservationRequestedAggregate decline test`() {
        val aggregate = ReservationRequestedAggregate(
            Id("test"), Id("customer"), Id("request"), Version(), Instant.DISTANT_PAST, Instant.DISTANT_FUTURE
        )
        val declineReservation = DeclineReservation(Id("test"))

        // Verify command
        val events = aggregate.verifyCommand(declineReservation)
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(ReservationDeclined::class.java)

        // Apply event
        val newAggregate = aggregate.applyEvent(events[0])
        assertThat(newAggregate).isInstanceOf(ReservationDeclinedAggregate::class.java)
        assertThat((newAggregate as ReservationDeclinedAggregate).reservationState).isEqualTo(ReservationState.DECLINED)
    }
}
