package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.V

interface RegistrationAgent {

    @SystemMessage(fromResource = "prompts/registration-agent-customer-registration-chat.txt")
    fun chat(@MemoryId memoryId: Id, @UserMessage message: String): String

    @UserMessage(
        """
            Does chat history contains any data related to the Custom data, is customer know?
        """
    )
    fun isCustomerIdentified(@MemoryId memoryId: Id): Boolean
}