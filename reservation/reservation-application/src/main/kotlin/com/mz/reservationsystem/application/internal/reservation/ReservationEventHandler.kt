package com.mz.reservationsystem.application.internal.reservation

import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.reservationsystem.domain.api.reservation.ReservationEvent

class ReservationEventHandler : AggregateEventHandler<ReservationAggregate, ReservationEvent> {
    override fun apply(aggregate: ReservationAggregate, event: ReservationEvent): ReservationAggregate {
        return aggregate.applyEvent(event)
    }
}