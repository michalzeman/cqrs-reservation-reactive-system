package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.localai.LocalAiChatModel
import dev.langchain4j.model.localai.LocalAiStreamingChatModel
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
@EnableConfigurationProperties(LlmChatModelProperties::class)
class LocalLlmModelConfiguration(
    private val properties: LlmChatModelProperties
) {

    @Bean
    fun localLlmModel(): ChatModel {
        return LocalAiChatModel.builder()
            .modelName(properties.model)
            .baseUrl(properties.baseUrl).logRequests(true)
            .maxTokens(2000)
            .temperature(0.1)
            .build()
    }

    @Bean
    fun localStreamingLlmModel(): StreamingChatModel {
        return LocalAiStreamingChatModel.builder()
            .modelName(properties.model)
            .baseUrl(properties.baseUrl)
            .maxTokens(2000)
            .temperature(0.2)
            .logResponses(true)
            .build()
    }

}