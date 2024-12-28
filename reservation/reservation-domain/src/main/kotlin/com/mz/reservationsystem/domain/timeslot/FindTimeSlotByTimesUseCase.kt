package com.mz.reservationsystem.domain.timeslot

import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class FindTimeSlotByTimesUseCase(
    private val timeSlotAggregateManager: TimeSlotAggregateManager,
    private val timeSlotView: TimeSlotView
) {

    operator fun invoke(query: TimeSlotQuery): Flux<TimeSlotDocument> {
        return timeSlotView.find(query)
            .take(10)
            .flatMap { timeSlotAggregateManager.findById(it) }
    }

}