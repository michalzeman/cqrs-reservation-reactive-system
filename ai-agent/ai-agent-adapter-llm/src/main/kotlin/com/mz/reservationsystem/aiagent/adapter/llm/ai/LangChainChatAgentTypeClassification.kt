package com.mz.reservationsystem.aiagent.adapter.llm.ai

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.ChatClassification
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.ConversationalChainProvider
import com.mz.reservationsystem.aiagent.adapter.llm.promptChatType
import com.mz.reservationsystem.aiagent.domain.ai.ChatAgentTypeClassification
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component


@Serializable
data class ChatUseCaseResult(val chatUseCase: ChatAgentType)

@Component
class LangChainChatAgentTypeClassification(
    private val chatClassification: ChatClassification,
    private val conversationalChainProvider: ConversationalChainProvider
) : ChatAgentTypeClassification {

    override suspend fun classify(chatId: Id, message: Content): ChatAgentType {
        val chatChain = conversationalChainProvider.chatChain<ChatUseCaseResult>(chatId)
        val result = chatChain(message.value)

        return result.chatUseCase
    }

    override suspend fun classify(message: Content): ChatAgentType {
        val param = mapOf("text" to message.value)
        val prompt = promptChatType.apply(param)

        return chatClassification.classifyChat(prompt.toUserMessage().singleText())
    }
}