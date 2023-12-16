package com.mz.reservationsystem.domain.api.reservation.command

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

sealed class ReservationCommand : DomainCommand {
    abstract val reservationId: Id
}

data class RequestReservation(
    override val reservationId: Id,
    val customerId: Id,
    val reservationRequestId: Id,
    val reservationTime: Instant,
    val numberOfPeople: Int = 0,
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val commandId: Id = newId()
) : ReservationCommand()

