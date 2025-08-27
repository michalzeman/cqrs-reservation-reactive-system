package com.mz.reservationsystem.aiagent.domain.ai.agent

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.Flow

interface ChatAgent {

    suspend fun userRegistrationChat(chatId: Id, message: Content): Flow<String>

    suspend fun reservationChat(chatId: Id, message: Content): Flow<String>

    suspend fun reservationViewChat(chatId: Id, message: Content): Flow<String>

    fun chatWithAssistant(chatId: Id, message: Content): Flow<String>

    fun chatWithCustomer(chatId: Id, message: Content): Flow<String>

}