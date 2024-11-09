package com.mz.reservationsystem.aiagent.adapter.customer

import com.mz.customer.adapter.rest.api.model.RegisterCustomerRequest
import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.customer.Customer
import com.mz.reservationsystem.aiagent.domain.customer.CustomerRepository
import com.mz.reservationsystem.aiagent.domain.customer.RegisterCustomer
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class BackedCustomerRepository(
    private val customerWebClient: WebClient
) : CustomerRepository {
    override suspend fun registerCustomer(registerCustomer: RegisterCustomer): Id {
        return customerWebClient.post()
            .bodyValue(registerCustomer.map())
            .retrieve()
            .bodyToMono(CustomerDocument::class.java)
            .map(CustomerDocument::aggregateId)
            .awaitSingle()
    }

    override suspend fun findCustomer(id: Id): Customer? = customerWebClient.get()
        .uri("/${id.value}")
        .retrieve()
        .bodyToMono<CustomerDocument>()
        .map { Customer(it) }
        .awaitSingleOrNull()
}

internal fun RegisterCustomer.map(): RegisterCustomerRequest {
    return RegisterCustomerRequest(
        lastName = lastName.value,
        firstName = firstName.value,
        email = email.value
    )
}