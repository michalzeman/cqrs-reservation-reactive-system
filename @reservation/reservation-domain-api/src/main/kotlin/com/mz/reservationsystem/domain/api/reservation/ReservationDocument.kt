package com.mz.reservationsystem.domain.api.reservation

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.Id
import kotlinx.datetime.Instant

data class ReservationDocument(
    val aggregateId: Id,
    override val correlationId: Id,
    override val createdAt: Instant,
    override val docId: Id,
    override val events: Set<ReservationEvent>
) : Document<ReservationEvent>