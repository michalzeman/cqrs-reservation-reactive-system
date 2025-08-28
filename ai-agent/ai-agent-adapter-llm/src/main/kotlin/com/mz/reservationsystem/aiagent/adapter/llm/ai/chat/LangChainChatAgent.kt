package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationAgent
import com.mz.reservationsystem.aiagent.application.ai.agent.ChatAgent
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Component

@Component
class LangChainChatAgent(
    private val assistantAgent: AssistantAgent,
    private val customerAgent: CustomerAgent,
    private val reservationAgent: ReservationAgent
) : ChatAgent {
    override suspend fun userRegistrationChat(chatId: Id, message: Content): Flow<String> =
        customerAgent.registrationChat(chatId, message.value).asFlow()

    override suspend fun reservationChat(chatId: Id, message: Content): Flow<String> =
        reservationAgent.createReservation(chatId, message.value).asFlow()


    override suspend fun reservationViewChat(chatId: Id, message: Content): Flow<String> =
        reservationAgent.listAllCustomerReservation(chatId, message.value).asFlow()

    override fun chatWithAssistant(chatId: Id, message: Content): Flow<String> =
        assistantAgent.chatStream(chatId, message.value).asFlow()

    override fun chatWithCustomer(chatId: Id, message: Content): Flow<String> =
        assistantAgent.chatWithCustomer(chatId, message.value).asFlow()
}