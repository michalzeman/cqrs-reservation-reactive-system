package com.mz.reservationsystem.aiagent.application.chat

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatCommand
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatDocument

interface ChatApi {
    suspend fun execute(cmd: ChatCommand): ChatDocument

    suspend fun findById(id: Id): ChatDocument?
}