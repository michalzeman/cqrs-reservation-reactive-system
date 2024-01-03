package com.mz.customer.domain

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.subscribeToChannel
import com.mz.customer.domain.api.CustomerCommand
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerEvent
import com.mz.customer.domain.api.UpdateCustomerReservationAsConfirmed
import com.mz.customer.domain.api.UpdateCustomerReservationAsDeclined
import com.mz.customer.domain.internal.Customer
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.reservationsystem.domain.api.reservation.ReservationAccepted
import com.mz.reservationsystem.domain.api.reservation.ReservationDeclined
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

/**
 * This class is responsible for the registration flow of a customer.
 * It uses the AggregateManager to handle the customer's commands and events.
 * It also uses the CustomerView to check if a customer already exists before registration.
 *
 * @property aggregateManager Handles the customer's commands and events.
 * @property customerView Used to check if a customer already exists before registration.
 */
@Component
class ReservationToCustomerFlow(
    private val aggregateManager: AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument>,
    applicationChannelStream: ApplicationChannelStream
) {

    init {
        applicationChannelStream.subscribeToChannel<ReservationDocument>(::invoke)
    }

    operator fun invoke(document: ReservationDocument): Mono<Void> {
        return when {
            document.isReservationAccepted() -> processReservationAccepted(document)
            document.isReservationDeclined() -> processReservationDeclined(document)
            else -> Mono.empty()
        }
    }

    fun processReservationAccepted(document: ReservationDocument): Mono<Void> {
        return document.toMono()
            .map {
                UpdateCustomerReservationAsConfirmed(
                    customerId = it.customerId,
                    reservationId = it.aggregateId,
                    requestId = it.requestId,
                    correlationId = it.correlationId
                )
            }.flatMap { aggregateManager.execute(it, it.customerId) }
            .then()
    }

    fun processReservationDeclined(document: ReservationDocument): Mono<Void> {
        return document.toMono()
            .map {
                UpdateCustomerReservationAsDeclined(
                    customerId = it.customerId,
                    reservationId = it.aggregateId,
                    requestId = it.requestId,
                    correlationId = it.correlationId
                )
            }.flatMap { aggregateManager.execute(it, it.customerId) }
            .then()
    }
}

internal fun ReservationDocument.isReservationAccepted(): Boolean {
    return events.any { it is ReservationAccepted }
}

internal fun ReservationDocument.isReservationDeclined(): Boolean {
    return events.any { it is ReservationDeclined }
}
