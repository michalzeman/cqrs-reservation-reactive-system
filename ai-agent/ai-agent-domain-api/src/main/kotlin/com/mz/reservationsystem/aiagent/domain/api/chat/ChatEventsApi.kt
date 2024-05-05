package com.mz.reservationsystem.aiagent.domain.api.chat

import com.mz.ddd.common.api.domain.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatEvent : DomainEvent {
    abstract val aggregateId: Id
}

@Serializable
@SerialName("chat-created")
data class ChatCreated(
    override val aggregateId: Id,
    override val correlationId: Id = newId(),
    override val eventId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : ChatEvent()

@Serializable
@SerialName("chat-message-added")
data class ChatMessageAdded(
    override val aggregateId: Id,
    val chatAiMessages: Set<ChatAiMessage>,
    override val correlationId: Id = newId(),
    override val eventId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : ChatEvent()

@Serializable
@SerialName("customer-id-added")
data class CustomerIdAdded(
    override val aggregateId: Id,
    val customerId: Id,
    override val correlationId: Id = newId(),
    override val eventId: Id = newId(),
    override val createdAt: Instant = instantNow()
) : ChatEvent()