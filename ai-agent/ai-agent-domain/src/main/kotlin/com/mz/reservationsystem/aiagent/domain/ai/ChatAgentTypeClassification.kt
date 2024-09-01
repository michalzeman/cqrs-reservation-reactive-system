package com.mz.reservationsystem.aiagent.domain.ai

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content

interface ChatAgentTypeClassification {

    suspend fun classify(chatId: Id, message: Content): ChatAgentType

    suspend fun classify(message: Content): ChatAgentType
}