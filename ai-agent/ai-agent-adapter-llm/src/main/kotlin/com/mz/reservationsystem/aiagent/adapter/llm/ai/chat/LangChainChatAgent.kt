package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.RegistrationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationAgent
import com.mz.reservationsystem.aiagent.domain.ai.agent.ChatAgent
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class LangChainChatAgent(
    private val assistantAgent: AssistantAgent,
    private val registrationAgent: RegistrationAgent,
    private val reservationAgent: ReservationAgent
) : ChatAgent {
    override suspend fun userRegistrationChat(chatId: Id, message: Content): String = withContext(Dispatchers.IO) {
        registrationAgent.chat(chatId, message.value)
    }

    override suspend fun reservationChat(chatId: Id, message: Content): String = withContext(Dispatchers.IO) {
        reservationAgent.createReservation(chatId, message.value)
    }

    override suspend fun reservationViewChat(chatId: Id, message: Content): String = coroutineScope {
        TODO("Not yet implemented")
    }

    override fun chatWithAssistant(chatId: Id, message: Content): Flow<String> =
        assistantAgent.chatStream(chatId, message.value).asFlow()

    override fun chatWithCustomer(chatId: Id, message: Content): Flow<String> =
        assistantAgent.chatWithCustomer(chatId, message.value).asFlow()
}