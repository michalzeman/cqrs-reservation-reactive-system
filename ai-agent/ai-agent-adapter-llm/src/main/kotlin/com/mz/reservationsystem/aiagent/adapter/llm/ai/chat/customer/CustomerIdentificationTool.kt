package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.application.customer.CustomerRepository
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

private val logger = LogFactory.getLog(CustomerIdentificationTool::class.java)

@Component
@Profile("!test-ai")
class CustomerIdentificationTool(
    private val customerRepository: CustomerRepository
) {

    @Tool("Find a customer")
    fun findCustomer(@P("customer id") customerId: String): String = runBlocking {
        logger.info("findCustomer -> $customerId")
        customerRepository.findCustomer(Id(customerId))
            ?.toString()
            ?: "User or customer for given ID doesn't exists!"
    }
}