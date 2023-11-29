package com.mz.customer.api.domain

import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.ddd.common.api.domain.*
import kotlinx.datetime.Instant

data class CustomerDocument(
    val aggregateId: Id,
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    val version: Version,
    val reservations: Set<Reservation> = emptySet(),
    override val docId: Id = newId(),
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val events: Set<CustomerEvent> = setOf()
) : Document<CustomerEvent>