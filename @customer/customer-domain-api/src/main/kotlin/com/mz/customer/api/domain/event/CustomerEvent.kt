package com.mz.customer.api.domain.event

import com.mz.ddd.common.api.domain.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed class CustomerEvent : DomainEvent {
    abstract val customerId: Id
}

@Serializable
data class CustomerRegistered(
    override val customerId: Id,
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()

@Serializable
data class CustomerReservationRequested(
    val reservationId: Id,
    override val customerId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()

@Serializable
data class CustomerReservationConfirmed(
    val reservationId: Id,
    override val customerId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()

@Serializable
data class CustomerReservationDeclined(
    val reservationId: Id,
    override val customerId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()