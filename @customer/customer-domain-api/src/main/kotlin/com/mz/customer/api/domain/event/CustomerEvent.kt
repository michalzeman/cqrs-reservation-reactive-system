package com.mz.customer.api.domain.event

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.uuid
import java.time.Instant

sealed interface CustomerEvent : DomainEvent {
    val customerId: String
}

data class CustomerRegistered(
    override val customerId: String,
    val lastName: String,
    val firstName: String,
    val email: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val eventId: String = uuid()
) : CustomerEvent

data class CustomerReservationRequested(
    val reservationId: String,
    override val customerId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val eventId: String = uuid()
) : CustomerEvent

data class CustomerReservationConfirmed(
    val reservationId: String,
    override val customerId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val eventId: String = uuid()
) : CustomerEvent

data class CustomerReservationDeclined(
    val reservationId: String,
    override val customerId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = Instant.now(),
    override val eventId: String = uuid()
) : CustomerEvent