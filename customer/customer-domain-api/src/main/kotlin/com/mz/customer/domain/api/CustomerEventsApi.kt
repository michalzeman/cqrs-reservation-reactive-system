package com.mz.customer.domain.api

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CustomerEvent : DomainEvent {
    abstract val aggregateId: Id
}

@Serializable
@SerialName("customer-registered")
data class CustomerRegistered(
    override val aggregateId: Id,
    val lastName: LastName,
    val firstName: FirstName,
    val email: Email,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()

@Serializable
@SerialName("customer-reservation-requested")
data class CustomerReservationRequested(
    val requestId: Id,
    val reservationPeriod: ReservationPeriod,
    override val aggregateId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()

@Serializable
@SerialName("customer-reservation-confirmed")
data class CustomerReservationConfirmed(
    val reservationId: Id,
    val requestId: Id,
    override val aggregateId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()

@Serializable
@SerialName("customer-reservation-declined")
data class CustomerReservationDeclined(
    val reservationId: Id,
    val requestId: Id,
    override val aggregateId: Id,
    override val correlationId: Id = Id(uuid()),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = Id(uuid())
) : CustomerEvent()