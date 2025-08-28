package com.mz.customer.application

import com.mz.common.components.ApplicationChannelStream
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerReservationConfirmed
import com.mz.customer.domain.api.RequestNewCustomerReservation
import com.mz.ddd.common.api.domain.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * This class is responsible for creating a reservation.
 * It sends a request to the reservation API to create a reservation and waits for the reservation to be accepted.
 */
@Component
class NewCustomerReservationUseCase(
    private val customerApi: CustomerApi,
    private val channelStream: ApplicationChannelStream
) {

    operator fun invoke(command: RequestNewCustomerReservation): Mono<CustomerDocument> {
        val reservationConfirmed = channelStream.messagesStream()
            .filter { it.isCorrelatedTo(command) }
            .cast(CustomerDocument::class.java)
            .take(1)
            .single()
            .timeout(Duration.ofSeconds(5))

        val customerApiExecution = customerApi.execute(command)

        return Mono.zip(reservationConfirmed, customerApiExecution)
            .map { it.t1 }
    }

}

internal fun Message.isCorrelatedTo(command: RequestNewCustomerReservation): Boolean = when (this) {
    is CustomerDocument -> this.aggregateId == command.customerId &&
            this.events.any { event ->
                event is CustomerReservationConfirmed &&
                        event.requestId == command.requestId
            }

    else -> false
}