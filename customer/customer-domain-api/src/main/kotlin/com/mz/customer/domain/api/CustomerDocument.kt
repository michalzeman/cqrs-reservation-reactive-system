package com.mz.customer.domain.api

import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.Version
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
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