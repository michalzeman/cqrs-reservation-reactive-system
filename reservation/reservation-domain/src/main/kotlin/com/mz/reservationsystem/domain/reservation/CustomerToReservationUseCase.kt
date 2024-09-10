package com.mz.reservationsystem.domain.reservation

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.subscribeToChannel
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerReservationRequested
import com.mz.reservationsystem.domain.api.reservation.NEW_RESERVATION_ID
import com.mz.reservationsystem.domain.api.reservation.RequestReservation
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomerToReservationUseCase(
    @Qualifier("reservationAggregateManager")
    private val aggregateManager: ReservationAggregateManager,
    channelStream: ApplicationChannelStream,
) {
    init {
        channelStream.subscribeToChannel<CustomerDocument>(::invoke)
    }

    operator fun invoke(document: CustomerDocument): Mono<Void> {
        return when {
            document.isCustomerReservationRequested() -> handleReservationRequested(document)
            else -> Mono.empty()
        }
    }

    private fun handleReservationRequested(document: CustomerDocument): Mono<Void> {
        return mono {
            val customerReservationRequested = document.getCustomerReservationRequested()
            RequestReservation(
                aggregateId = NEW_RESERVATION_ID,
                customerId = customerReservationRequested.aggregateId,
                requestId = customerReservationRequested.reservationId,
                startTime = customerReservationRequested.reservationPeriod.startTime,
                endTime = customerReservationRequested.reservationPeriod.endTime,
                correlationId = customerReservationRequested.correlationId
            )
        }.flatMap { cmd ->
            aggregateManager.execute(cmd, cmd.aggregateId)
        }.then()
    }

}

internal fun CustomerDocument.isCustomerReservationRequested(): Boolean {
    return events.any { it is CustomerReservationRequested }
}

internal fun CustomerDocument.getCustomerReservationRequested(): CustomerReservationRequested {
    return events.find { it is CustomerReservationRequested }?.let { it as CustomerReservationRequested }!!
}