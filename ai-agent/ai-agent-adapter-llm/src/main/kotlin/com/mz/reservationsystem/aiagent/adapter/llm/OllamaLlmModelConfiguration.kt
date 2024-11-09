package com.mz.reservationsystem.aiagent.adapter.llm

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("ollama")
@Configuration
class OllamaLlmModelConfiguration(
    @Value("\${adapter.llm.chat.base-url}") private val llmChatBaseUrl: String,
    @Value("\${adapter.llm.chat.model}") private val chatModel: String,
) {

    @Bean
    fun ollamaLlmModel(): ChatLanguageModel {
        return OpenAiChatModel.builder()
            .apiKey("***")
            .modelName(chatModel)
            .maxRetries(5)
            .logRequests(true)
            .logResponses(true)
            .baseUrl(llmChatBaseUrl)
            .temperature(0.1)
            .build()
    }

    @Bean
    fun ollamaStreamingLlmModel(): StreamingChatLanguageModel {
        return OpenAiStreamingChatModel.builder()
            .apiKey("***")
            .modelName(chatModel)
            .baseUrl(llmChatBaseUrl)
            .logRequests(true)
            .logResponses(true)
            .parallelToolCalls(true)
            .temperature(0.1)
            .build()
    }

}