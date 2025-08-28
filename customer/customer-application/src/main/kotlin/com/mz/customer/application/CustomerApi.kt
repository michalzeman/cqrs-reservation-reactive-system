package com.mz.customer.application

import com.mz.customer.domain.api.CustomerCommand
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerEvent
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.application.internal.Customer
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class CustomerApi(
    private val aggregateManager: AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument>,
    private val registerCustomerUseCase: RegisterCustomerUseCase
) {
    fun execute(cmd: CustomerCommand): Mono<CustomerDocument> {
        return when (cmd) {
            is RegisterCustomer -> registerCustomerUseCase(cmd)

            else -> aggregateManager.execute(cmd, cmd.customerId)
        }
    }

    fun findById(id: Id): Mono<CustomerDocument> {
        return aggregateManager.findById(id)
    }
}