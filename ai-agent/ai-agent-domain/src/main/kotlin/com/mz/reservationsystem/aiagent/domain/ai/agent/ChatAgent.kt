package com.mz.reservationsystem.aiagent.domain.ai.agent

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import java.time.Duration

interface ChatAgent {

    suspend fun userRegistrationChat(chatId: Id, message: Content): String

    suspend fun reservationChat(chatId: Id, message: Content): String

    suspend fun reservationViewChat(chatId: Id, message: Content): String

    fun chatWithAssistant(chatId: Id, message: Content): Flow<String>

    fun chatWithCustomer(chatId: Id, message: Content): Flow<String>

}

fun String.asFlow(): Flow<String> = split(Regex("\\s"))
    .map { "$it " }
    .asFlow()
    .onEach { delay(Duration.ofMillis(50).toMillis()) }