package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.localai.LocalAiChatModel
import dev.langchain4j.model.localai.LocalAiStreamingChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
class LocalLlmModelConfiguration(
    @Value("\${adapter.llm.chat.base-url}") private val llmChatBaseUrl: String,
    @Value("\${adapter.llm.chat.model}") private val chatModel: String,
) {

    @Bean
    fun localLlmModel(): ChatLanguageModel {
        return LocalAiChatModel.builder()
            .modelName(chatModel)
            .baseUrl(llmChatBaseUrl).logRequests(true)
            .maxTokens(2000)
            .temperature(0.1)
            .build()
    }

    @Bean
    fun localStreamingLlmModel(): StreamingChatLanguageModel {
        return LocalAiStreamingChatModel.builder()
            .modelName(chatModel)
            .baseUrl(llmChatBaseUrl)
            .maxTokens(2000)
            .temperature(0.2)
            .logResponses(true)
            .build()
    }

}