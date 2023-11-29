package com.mz.customer.domain

import com.mz.customer.api.domain.CustomerDocument
import com.mz.customer.api.domain.command.CustomerCommand
import com.mz.customer.api.domain.event.CustomerEvent
import com.mz.customer.domain.internal.Customer
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class CustomerApi(
    private val aggregateManager: AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument>
) {
    fun execute(cmd: CustomerCommand): Mono<CustomerDocument> {
        return aggregateManager.execute(cmd, cmd.customerId)
    }

    fun findById(id: Id): Mono<CustomerDocument> {
        return aggregateManager.findById(id)
    }
}