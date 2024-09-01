package com.mz.reservationsystem.aiagent.domain.ai.model

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AgentRequest {
    abstract val message: Content
}

@Serializable
sealed class AgentResponse {
    abstract val chatId: Id
    abstract val message: Content
}

@Serializable
@SerialName("new-chat-request")
data class NewChatRequest(override val message: Content) : AgentRequest()

@Serializable
@SerialName("new-chat-customer-request")
data class NewChatCustomerRequest(
    override val message: Content,
    val customerId: Id,
    val customerData: Content
) : AgentRequest()

@Serializable
@SerialName("chat-request")
data class ChatRequest(
    val chatId: Id,
    override val message: Content
) : AgentRequest()

@Serializable
@SerialName("chat-customer-request")
data class ChatCustomerRequest(
    override val message: Content,
    val chatId: Id,
    val customerId: Id,
    val customerData: Content
) : AgentRequest()

@Serializable
@SerialName("chat-response")
data class ChatResponse(
    override val chatId: Id,
    override val message: Content,
) : AgentResponse()

@Serializable
@SerialName("redirect-response")
data class RedirectResponse(
    override val chatId: Id,
    override val message: Content,
    val action: ChatAgentType,
) : AgentResponse()