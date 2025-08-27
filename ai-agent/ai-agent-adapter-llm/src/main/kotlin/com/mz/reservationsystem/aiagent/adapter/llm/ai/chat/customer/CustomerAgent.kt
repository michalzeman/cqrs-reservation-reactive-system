package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import reactor.core.publisher.Flux

interface CustomerAgent {

    @SystemMessage(fromResource = "prompts/registration-agent-customer-registration-chat.txt")
    fun registrationChat(@MemoryId memoryId: Id, @UserMessage message: String): Flux<String>
}