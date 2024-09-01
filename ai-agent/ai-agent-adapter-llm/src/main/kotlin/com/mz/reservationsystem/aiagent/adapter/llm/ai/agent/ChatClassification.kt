package com.mz.reservationsystem.aiagent.adapter.llm.ai.agent

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import kotlinx.serialization.Serializable

@Serializable
data class ClassificationResult(val result: Boolean, val reason: String)

interface ChatClassification {

    @UserMessage(
        """
        Is the following text asking for operation: "create new customer"
        You must respond strictly true or false.
        You can't warp response in backticks, or any other wrapping character.
        Text: {{it}}
     """
    )
    fun isRelatedToReservationSystem(text: String): Boolean

    @UserMessage(
        """
         Is the following text asking for operation: "create new customer"
         You must respond in a valid JSON format.
         You must not wrap JSON response in backticks, markdown, or in any other way, but return it as plain text.
         JSON structure:
         {
            "result": "Boolean",
            "reason": "String"
         }
         Text: {{it}}
        """
    )
    fun relatedToReservationSystem(text: String): ClassificationResult

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
    fun chat(@MemoryId memoryId: Id, @UserMessage message: String): String

    fun classifyChat(@UserMessage message: String): ChatAgentType

}
