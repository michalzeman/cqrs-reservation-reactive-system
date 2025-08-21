package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat

import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
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
        "You are classification agent, acting as simple classification function. Answer with single word."
    )
    fun classifyChat(@UserMessage message: String): ChatAgentType

}
