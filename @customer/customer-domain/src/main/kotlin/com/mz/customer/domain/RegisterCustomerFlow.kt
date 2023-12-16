package com.mz.customer.domain

import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.command.CustomerCommand
import com.mz.customer.domain.api.command.RegisterCustomer
import com.mz.customer.domain.api.event.CustomerEvent
import com.mz.customer.domain.internal.Customer
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * This class is responsible for the registration flow of a customer.
 * It uses the AggregateManager to handle the customer's commands and events.
 * It also uses the CustomerView to check if a customer already exists before registration.
 *
 * @property aggregateManager Handles the customer's commands and events.
 * @property customerView Used to check if a customer already exists before registration.
 */
@Component
class RegisterCustomerFlow(
    private val aggregateManager: AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument>,
    private val customerView: CustomerView
) {
    operator fun invoke(cmd: RegisterCustomer): Mono<CustomerDocument> {
        val exists = customerView.find(FindCustomerByEmail(cmd.email)).count()

        return exists.flatMap { ex ->
            if (ex > 0) error("Customer already exist")
            else aggregateManager.execute(cmd, cmd.customerId)
        }
    }
}