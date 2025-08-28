package com.mz.reservationsystem.aiagent.application.chat.intenal

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.AggregateManager
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatCommand
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatDocument
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatEvent
import com.mz.reservationsystem.aiagent.application.chat.ChatApi
import com.mz.reservationsystem.aiagent.application.chat.aggregate.Chat
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
internal class ChatApiImpl(
    @Qualifier("chatAggregateManager")
    private val aggregateManager: AggregateManager<Chat, ChatCommand, ChatEvent, ChatDocument>
) : ChatApi {

    override suspend fun execute(cmd: ChatCommand): ChatDocument {
        return aggregateManager.execute(cmd, cmd.aggregateId)
            .awaitSingle()
    }

    override suspend fun findById(id: Id): ChatDocument? {
        return aggregateManager.findById(id)
            .awaitSingleOrNull()
    }
}