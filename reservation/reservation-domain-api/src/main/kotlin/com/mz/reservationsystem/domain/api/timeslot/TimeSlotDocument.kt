package com.mz.reservationsystem.domain.api.timeslot

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class TimeSlotDocument(
    val aggregateId: Id,
    val startTime: Instant,
    val endTime: Instant,
    val booked: Boolean,
    val valid: Boolean,
    val reservationId: Id? = null,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val docId: Id = newId(),
    override val events: Set<TimeSlotEvent> = setOf()
) : Document<TimeSlotEvent> {
}