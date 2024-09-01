package com.mz.reservationsystem.aiagent.domain.ai

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.ai.agent.ChatAgent
import com.mz.reservationsystem.aiagent.domain.ai.agent.asFlow
import com.mz.reservationsystem.aiagent.domain.ai.model.AgentResponse
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import com.mz.reservationsystem.aiagent.domain.api.chat.UpdateChatAgent
import com.mz.reservationsystem.aiagent.domain.chat.ChatApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class AgentChatRoute(
    private val chatApi: ChatApi,
    private val chatAgentTypeClassification: ChatAgentTypeClassification,
    private val chatAgent: ChatAgent
) {

    /**
     * Routes the chat message to the appropriate handler based on the message content.
     *
     * @param id The unique identifier for the chat session.
     * @param message The content of the chat message.
     * @param defaultChat A lambda function that returns a Flow of AgentResponse, used as the default chat handler.
     * @return A Flow of AgentResponse representing the routed chat responses.
     */
    fun routeChat(id: Id, message: Content, defaultChat: () -> Flow<AgentResponse>): Flow<AgentResponse> = flow {
        val toChatResponse = { text: String -> ChatResponse(id, Content(text)) }

        val chatAgentType = chatApi.findById(id)
            ?.takeIf { it.chatAgentType != ChatAgentType.NONE }
            ?.chatAgentType
            ?: classifyChatAgentType(id, message)

        emitAll(when (chatAgentType) {
            ChatAgentType.USER_REGISTRATION -> chatAgent.userRegistrationChat(id, message)
                .asFlow()
                .map { toChatResponse(it) }

            ChatAgentType.RESERVATION -> chatAgent.reservationChat(id, message)
                .asFlow()
                .map { toChatResponse(it) }

            ChatAgentType.RESERVATION_VIEW -> chatAgent.reservationViewChat(id, message)
                .asFlow()
                .map { toChatResponse(it) }

            ChatAgentType.NONE -> defaultChat()
        })
    }

    private suspend fun classifyChatAgentType(id: Id, message: Content): ChatAgentType {
        logger.trace("Classifying chat agent type for chat $id with message: $message")
        val result = chatAgentTypeClassification.classify(message)
        updateChat(id, result)
        logger.info("Chat $id classified as $result")
        return result
    }

    private suspend fun updateChat(chatId: Id, chatAgentType: ChatAgentType) {
        chatApi.execute(UpdateChatAgent(chatId, chatAgentType))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AgentChatRoute::class.java)
    }
}
