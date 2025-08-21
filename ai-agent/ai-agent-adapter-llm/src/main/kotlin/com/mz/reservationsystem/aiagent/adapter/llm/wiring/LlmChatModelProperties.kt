package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("adapter.llm.chat")
data class LlmChatModelProperties(
    val baseUrl: String,
    val model: String,
    val smallModel: String,
    val temperature: Double
)