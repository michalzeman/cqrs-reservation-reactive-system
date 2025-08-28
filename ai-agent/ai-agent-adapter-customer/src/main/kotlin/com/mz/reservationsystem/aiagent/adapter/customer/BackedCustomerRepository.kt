package com.mz.reservationsystem.aiagent.adapter.customer

import com.mz.customer.adapter.rest.api.model.NewCustomerReservationRequest
import com.mz.customer.adapter.rest.api.model.RegisterCustomerRequest
import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.uuid
import com.mz.reservationsystem.aiagent.application.customer.CreateReservation
import com.mz.reservationsystem.aiagent.application.customer.Customer
import com.mz.reservationsystem.aiagent.application.customer.CustomerRepository
import com.mz.reservationsystem.aiagent.application.customer.RegisterCustomer
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.datetime.toKotlinInstant
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class BackedCustomerRepository(
    private val customerWebClient: WebClient
) : CustomerRepository {
    override suspend fun registerCustomer(registerCustomer: RegisterCustomer): CustomerDocument =
        customerWebClient.post()
            .bodyValue(registerCustomer.map())
            .retrieve()
            .bodyToMono<CustomerDocument>()
            .awaitSingle()

    override suspend fun findCustomer(id: Id): Customer? =
        customerWebClient.get()
            .uri("/${id.value}")
            .retrieve()
            .bodyToMono<CustomerDocument>()
            .map { Customer(it) }
            .awaitSingleOrNull()

    override suspend fun createReservation(data: CreateReservation): Id {
        val request = NewCustomerReservationRequest(
            customerId = data.customerId.value,
            requestId = uuid(),
            startTime = data.startTime.toKotlinInstant(),
            endTime = data.endTime.toKotlinInstant()
        )

        return customerWebClient.put()
            .uri {
                it.pathSegment(request.customerId, "reservations").build()
            }
            .bodyValue(request)
            .retrieve()
            .bodyToMono<CustomerDocument>()
            .map { customer ->
                customer.reservations
                    .find { it.requestId.value == request.requestId }
                    ?.id
                    ?: error("Unable to confirmed that requested reservation is accepted")
            }
            .awaitSingle()
    }
}

internal fun RegisterCustomer.map(): RegisterCustomerRequest {
    return RegisterCustomerRequest(
        lastName = lastName.value,
        firstName = firstName.value,
        email = email.value
    )
}