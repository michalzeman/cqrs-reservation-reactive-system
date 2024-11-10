package com.mz.reservationsystem.aiagent.domain.ai

import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.domain.ai.model.*
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import org.springframework.stereotype.Component

// TODO: reconsider this approach -> not sure if we need to add for the every time user information
internal val customerPrompt: (message: Content, customerData: Content) -> String = { message, customerData ->
    """
    `customer metadata`: 
    ```json
    ${customerData.value}
    ```
    `instruction for response`:
    * Use the content within the `<message>` tags for interaction.
    * Treat the `customer metadata` section as context and do not include it in the response.
    * Write all important information in markdown format.
    * Exclude the following fields from the response:
    ** correlationId
    ** docId
    ** createdAt
    ** events
    ** version
    <message>${message.value}</message>
    """.trimIndent()
}

@Component
class AgentManager(
    private val agentChatRoute: AgentChatRoute,
) {

    fun execute(request: AgentRequest, finished: () -> Unit = {}): Flow<AgentResponse> = when (request) {
        is NewChatRequest -> handleUnknownUserRequest(request)
        is ChatRequest -> handleChatRequest(request)
        is ChatCustomerRequest -> handleChatCustomerRequest(request)
        is NewChatCustomerRequest -> handleNewChatCustomerRequest(request)
    }.onCompletion { finished() }

    private fun handleChatCustomerRequest(request: ChatCustomerRequest): Flow<AgentResponse> {
        val message = Content(customerPrompt(request.message, request.customerData))
        val chatId = request.chatId
        return agentChatRoute.routeChat(chatId, message)
    }

    private fun handleNewChatCustomerRequest(request: NewChatCustomerRequest): Flow<AgentResponse> {
        val message = Content(customerPrompt(request.message, request.customerData))
        val chatId = newId()
        return agentChatRoute.routeChat(chatId, message)
    }

    private fun handleUnknownUserRequest(request: NewChatRequest): Flow<AgentResponse> {
        val id = newId()
        return agentChatRoute.routeChat(id, request.message)
    }

    private fun handleChatRequest(request: ChatRequest): Flow<AgentResponse> {
        val id = request.chatId
        return agentChatRoute.routeChat(id, request.message)
    }
}