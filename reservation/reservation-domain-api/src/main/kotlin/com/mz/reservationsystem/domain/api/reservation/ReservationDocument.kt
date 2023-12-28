package com.mz.reservationsystem.domain.api.reservation

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

enum class ReservationState {
    REQUESTED,
    DECLINED,
    ACCEPTED
}

data class ReservationDocument(
    val aggregateId: Id,
    val reservationState: ReservationState,
    val customerId: Id,
    val requestId: Id,
    val version: Version,
    val startTime: Instant,
    val endTime: Instant,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val docId: Id = newId(),
    override val events: Set<ReservationEvent> = emptySet()
) : Document<ReservationEvent>