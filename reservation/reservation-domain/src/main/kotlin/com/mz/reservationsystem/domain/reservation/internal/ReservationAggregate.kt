package com.mz.reservationsystem.domain.reservation.internal

import com.mz.ddd.common.api.domain.Aggregate
import com.mz.ddd.common.api.domain.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ReservationAggregate : Aggregate()

@Serializable
@SerialName("none-reservation-aggregate")
internal data class NoneReservationAggregate(override val aggregateId: Id) : ReservationAggregate()

@Serializable
@SerialName("some-reservation-aggregate")
internal data class SomeReservationAggregate(override val aggregateId: Id) : ReservationAggregate()