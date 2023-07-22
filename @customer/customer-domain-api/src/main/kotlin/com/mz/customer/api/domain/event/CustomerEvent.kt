package com.mz.customer.api.domain.event

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Polymorphic
sealed class CustomerEvent : DomainEvent() {
    abstract val customerId: String
}

@Serializable
data class CustomerRegistered(
    override val customerId: String,
    val lastName: String,
    val firstName: String,
    val email: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val eventId: String = uuid()
) : CustomerEvent()

@Serializable
data class CustomerReservationRequested(
    val reservationId: String,
    override val customerId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val eventId: String = uuid()
) : CustomerEvent()

@Serializable
data class CustomerReservationConfirmed(
    val reservationId: String,
    override val customerId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val eventId: String = uuid()
) : CustomerEvent()

@Serializable
data class CustomerReservationDeclined(
    val reservationId: String,
    override val customerId: String,
    override val correlationId: String = uuid(),
    override val createdAt: Instant = instantNow(),
    override val eventId: String = uuid()
) : CustomerEvent()