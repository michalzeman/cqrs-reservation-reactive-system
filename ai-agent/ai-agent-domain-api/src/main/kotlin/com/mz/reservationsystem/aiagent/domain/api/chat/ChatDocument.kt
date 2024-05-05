package com.mz.reservationsystem.aiagent.domain.api.chat

import com.mz.ddd.common.api.domain.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatDocument(
    val aggregateId: Id,
    val customerId: Id? = null,
    val chatAiMessages: Set<ChatAiMessage> = setOf(),
    override val events: Set<ChatEvent> = setOf(),
    override val correlationId: Id = newId(),
    override val docId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    val version: Version
) : Document<ChatEvent>