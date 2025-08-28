package com.mz.customer.application

import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.Id
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

object CustomerProperties {
    val email = "email"
}

sealed interface CustomerQuery

data class FindCustomerByEmail(
    val email: Email
) : CustomerQuery

interface CustomerView {
    fun process(document: CustomerDocument): Mono<Void>

    fun find(query: CustomerQuery): Flux<Id>
}