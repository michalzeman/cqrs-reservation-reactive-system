package com.mz.customer.domain.internal.view

import com.mz.customer.domain.CustomerProperties
import com.mz.customer.domain.CustomerQuery
import com.mz.customer.domain.CustomerView
import com.mz.customer.domain.FindCustomerByEmail
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.internal.CUSTOMER_DOMAIN_TAG
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.query.*
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CustomerViewImpl(
    private val domainViewRepository: DomainViewRepository,
    private val domainViewReadOnlyRepository: DomainViewReadOnlyRepository
) : CustomerView {

    override fun process(document: CustomerDocument): Mono<Void> {
        val email = QueryableString(
            value = document.email.value,
            aggregateId = document.aggregateId.value,
            domainTag = CUSTOMER_DOMAIN_TAG.value,
            propertyName = CustomerProperties.email,
            timestamp = instantNow()
        )
        return domainViewRepository.save(setOf(email))
    }

    override fun find(query: CustomerQuery): Flux<Id> {
        when (query) {
            is FindCustomerByEmail -> {
                val requestQuery = DomainViewQuery(
                    setOf(
                        QueryString(
                            CustomerProperties.email,
                            CUSTOMER_DOMAIN_TAG.value,
                            query.email.value
                        )
                    )
                )
                return domainViewReadOnlyRepository.find(
                    requestQuery
                ).map { it.aggregateId }
            }
        }
    }
}