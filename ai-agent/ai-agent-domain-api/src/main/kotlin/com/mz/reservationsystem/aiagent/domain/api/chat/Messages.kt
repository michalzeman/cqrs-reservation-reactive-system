package com.mz.reservationsystem.aiagent.domain.api.chat

import kotlinx.serialization.Serializable

typealias JsonContent = Map<String, String>

@JvmInline
@Serializable
value class Content(val value: String)

@Serializable
data class ChatAiMessage(val content: Content)