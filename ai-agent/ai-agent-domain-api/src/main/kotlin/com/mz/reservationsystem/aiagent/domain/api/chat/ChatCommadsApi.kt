package com.mz.reservationsystem.aiagent.domain.api.chat

import com.mz.ddd.common.api.domain.DomainCommand
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant

val NEW_CHAT_ID = Id("new-chat-id")

sealed class ChatCommand : DomainCommand {
    abstract val aggregateId: Id
}

data class CreateChat(
    override val aggregateId: Id = NEW_CHAT_ID,
    override val commandId: Id = newId(),
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : ChatCommand()

fun CreateChat.toEvent(): ChatCreated {
    return ChatCreated(
        aggregateId = this.aggregateId,
        correlationId = this.correlationId,
    )
}

data class AddChatMessage(
    override val aggregateId: Id,
    val chatAiMessages: Set<ChatAiMessage>,
    override val commandId: Id = newId(),
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : ChatCommand()

fun AddChatMessage.toEvent(): ChatMessageAdded {
    return ChatMessageAdded(
        aggregateId = this.aggregateId,
        correlationId = this.correlationId,
        chatAiMessages = this.chatAiMessages
    )
}

data class AddCustomerId(
    override val aggregateId: Id,
    val customerId: Id,
    override val commandId: Id = newId(),
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : ChatCommand()

fun AddCustomerId.toEvent(): CustomerIdAdded {
    return CustomerIdAdded(
        aggregateId = this.aggregateId,
        correlationId = this.correlationId,
        customerId = this.customerId
    )
}