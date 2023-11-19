package com.mz.customer.api.domain

import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.ddd.common.api.domain.Document
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

data class CustomerDocument(
    override val docId: Id = newId(),
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val events: Set<CustomerEvent> = setOf()
) : Document<CustomerEvent>