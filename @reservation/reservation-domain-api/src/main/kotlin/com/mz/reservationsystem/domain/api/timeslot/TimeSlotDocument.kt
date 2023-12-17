package com.mz.reservationsystem.domain.api.timeslot

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.Id
import kotlinx.datetime.Instant

class TimeSlotDocument(
    val aggregateId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    override val correlationId: Id,
    override val createdAt: Instant,
    override val docId: Id,
    override val events: Set<TimeSlotEvent>
) : Document<TimeSlotEvent> {
}