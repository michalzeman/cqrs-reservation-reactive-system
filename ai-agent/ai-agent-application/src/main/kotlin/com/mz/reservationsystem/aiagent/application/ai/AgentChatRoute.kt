package com.mz.reservationsystem.aiagent.application.ai

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.application.ai.agent.ChatAgent
import com.mz.reservationsystem.aiagent.application.ai.model.AgentResponse
import com.mz.reservationsystem.aiagent.application.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import com.mz.reservationsystem.aiagent.domain.api.chat.UpdateChatAgent
import com.mz.reservationsystem.aiagent.application.chat.ChatApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
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
    fun routeChat(id: Id, message: Content): Flow<AgentResponse> {
//        = flow {
        val toChatResponse = { text: String -> ChatResponse(id, Content(text)) }

        return flow {
            val chatAgentType = chatApi.findById(id)
                ?.takeIf { it.chatAgentType != ChatAgentType.NONE }
                ?.chatAgentType
                ?: classifyChatAgentType(id, message)
            emit(chatAgentType)
        }.flatMapConcat { chatAgentType ->
//        emitAll(
            when (chatAgentType) {
                ChatAgentType.USER_REGISTRATION -> {
                    logger.info("userRegistrationChat ->")
                    chatAgent.userRegistrationChat(id, message)
                        .map { toChatResponse(it) }
                }

                ChatAgentType.RESERVATION -> {
                    logger.info("reservationChat ->")
                    chatAgent.reservationChat(id, message)
                        .map { toChatResponse(it) }
                }

                ChatAgentType.RESERVATION_VIEW -> {
                    logger.info("reservationViewChat ->")
                    chatAgent.reservationViewChat(id, message)
                        .map { toChatResponse(it) }
                }

                ChatAgentType.NONE -> {
                    logger.info("chatWithAssistant ->")
                    chatAgent.chatWithAssistant(id, message)
                        .map { toChatResponse(it) }
                }
            }
        }
    }

    private suspend fun classifyChatAgentType(id: Id, message: Content): ChatAgentType {
        logger.info("Classifying chat agent type for chat $id with message: $message")
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
