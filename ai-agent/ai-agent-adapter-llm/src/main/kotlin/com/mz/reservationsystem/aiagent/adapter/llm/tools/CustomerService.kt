package com.mz.reservationsystem.aiagent.adapter.llm.tools

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component


data class Customer(
    @P("First name of the customer") val firstName: String,
    @P("Last name of the customer") val lastName: String,
    @P("Email of the customer") val email: String
)

data class CustomerAccount(val customer: Customer, val email: String, val password: String, val id: String) {
    init {
        requireNotNull(customer.firstName) { "First name is required" }
        requireNotNull(customer.lastName) { "Last name is required" }
        requireNotNull(email) { "Email is required" }
    }
}

@Component
class CustomerService {

    companion object {
        private val logger = LogFactory.getLog(CustomerService::class.java)
    }

    @Tool("Register a new customer")
    fun registerCustomer(
        @P("Customer object supports firstName, lastName, email as mandatory fields") customer: Customer
    ): String {
        logger.info("registerCustomer -> $customer")
        return CustomerAccount(customer, customer.email, "password", "id").toString()
    }

    @Tool("List if the reservations identified by url")
    fun getWebPageContent(@P("URL of the reservation") url: String): String {
        return "Test zemo, nothing there"
    }
}