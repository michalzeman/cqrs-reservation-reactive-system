package com.mz.customer.application

import com.mz.customer.domain.api.CustomerCommand
import com.mz.customer.domain.api.CustomerDocument
import com.mz.customer.domain.api.CustomerEvent
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.customer.application.internal.Customer
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * This class is responsible for the registration use-case of a customer.
 * It uses the AggregateManager to handle the customer's commands and events.
 * It also uses the CustomerView to check if a customer already exists before registration.
 *
 * @property aggregateManager Handles the customer's commands and events.
 * @property customerView Used to check if a customer already exists before registration.
 */
@Component
class RegisterCustomerUseCase(
    private val aggregateManager: AggregateManager<Customer, CustomerCommand, CustomerEvent, CustomerDocument>,
    private val customerView: CustomerView
) {
    operator fun invoke(cmd: RegisterCustomer): Mono<CustomerDocument> {
        val verifyOfExistingCustomer = customerView.find(FindCustomerByEmail(cmd.email)).count()
            .map { count ->
                if (count > 0) error("Customer already exist")
                else true
            }
        return aggregateManager.execute(cmd, cmd.customerId, { verifyOfExistingCustomer })
    }
}