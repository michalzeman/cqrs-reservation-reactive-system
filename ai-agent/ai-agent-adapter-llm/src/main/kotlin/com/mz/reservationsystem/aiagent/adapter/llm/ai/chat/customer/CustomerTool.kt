package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer

import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.FirstName
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.LastName
import com.mz.reservationsystem.aiagent.domain.customer.CustomerRepository
import com.mz.reservationsystem.aiagent.domain.customer.RegisterCustomer
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.model.output.structured.Description
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Description("Customer registration parameters")
data class CustomerParam(
    @Description("First name of the customer") val firstName: String,
    @Description("Last name of the customer") val lastName: String,
    @Description("Email of the customer") val email: String
)

data class CustomerAccount(val customer: CustomerParam, val id: String) {
    init {
        requireNotNull(customer.firstName) { "First name is required" }
        requireNotNull(customer.lastName) { "Last name is required" }
        requireNotNull(customer.email) { "Email is required" }
    }
}

private val logger = LogFactory.getLog(CustomerTool::class.java)

@Component
class CustomerTool(
    private val customerRepository: CustomerRepository
) {

    @Tool("Register a new customer")
    fun registerCustomer(
        @P("Customer object supports firstName, lastName, email as mandatory fields") customer: CustomerParam
    ): String = runBlocking {
        logger.info("registerCustomer -> $customer")
        val createdCustomer = customerRepository.registerCustomer(customer.toRegisterCustomer())
        CustomerAccount(customer, createdCustomer.aggregateId.value).toString()
    }

    @Tool("Find customer")
    fun findCustomer(@P("customer id") customerId: String): String = runBlocking {
        logger.info("findCustomer -> $customerId")
        customerRepository.findCustomer(Id(customerId))?.toString()
            ?: "User or customer for given ID doesn't exists!"
    }
}

internal fun CustomerParam.toRegisterCustomer(): RegisterCustomer = RegisterCustomer(
    LastName(lastName),
    FirstName(firstName),
    Email(email)
)