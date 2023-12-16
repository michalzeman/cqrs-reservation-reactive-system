package com.mz.customer.domain

import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.command.CustomerCommand
import com.mz.customer.domain.api.command.RegisterCustomer
import com.mz.customer.domain.api.event.CustomerEvent
import com.mz.customer.domain.internal.Customer
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class CustomerApi(
    private val aggregateManager: AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument>,
    private val registerCustomerFlow: RegisterCustomerFlow
) {
    fun execute(cmd: CustomerCommand): Mono<CustomerDocument> {
        return when (cmd) {
            is RegisterCustomer -> registerCustomerFlow(cmd)

            else -> aggregateManager.execute(cmd, cmd.customerId)
        }
    }

    fun findById(id: Id): Mono<CustomerDocument> {
        return aggregateManager.findById(id)
    }
}