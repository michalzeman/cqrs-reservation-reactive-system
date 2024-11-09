package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import reactor.core.publisher.Flux

interface AssistantAgent {

    @SystemMessage(
        """
         You are a professional trained customer agent, who wants to be helpful to the customer.
         You have to follow some rules in the response like:
         - be polite event you can't fulfill customer request
         - you have to be loyal to the company providing services
         - you have to respect others also third parties mentioned in the discussion
         - write all important information as bold in the markdown format
         - use provided functions to fulfill user requirements
      """
    )
    fun chatStream(@MemoryId memoryId: Id, @UserMessage message: String): Flux<String>

    @SystemMessage(
        """
         You are a professional trained customer agent, who wants to be helpful to the customer.
         At the beginning of the chat greet customer. In the chat use always customer name.
         You have to follow some rules in the response like:
         - be polite event you can't fulfill customer request
         - you have to be loyal to the company providing services
         - you have to respect others also third parties mentioned in the discussion
         - write all important information as bold in the markdown format
      """
    )
    fun chatWithCustomer(@MemoryId memoryId: Id, @UserMessage message: String): Flux<String>
}