package com.mz.reservationsystem.aiagent.adapter.llm

import com.mz.reservationsystem.aiagent.domain.Assistant
import com.mz.reservationsystem.aiagent.domain.agent.ChatClassification
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.localai.LocalAiChatModel
import dev.langchain4j.model.localai.LocalAiStreamingChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
class LlmModelConfiguration(
    @Value("\${adapter.llm.chat.base-url}") private val llmChatBaseUrl: String,
    @Value("\${adapter.llm.chat.model}") private val chatModel: String,
) {

    @Profile("local")
    @Bean
    fun localClassificationLlmModel(): ChatLanguageModel {
        return LocalAiChatModel.builder()
            .modelName(chatModel)
            .baseUrl(llmChatBaseUrl).logRequests(true)
            .maxTokens(100)
            .temperature(0.1)
            .build()
    }

    @Profile("local")
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

    @Profile("ollama")
    @Bean
    fun ollamaClassificationLlmModel(): ChatLanguageModel {
        return LocalAiChatModel.builder()
            .modelName(chatModel)
            .baseUrl(llmChatBaseUrl).logRequests(true)
            .maxTokens(100)
            .temperature(0.1)
            .build()
    }

    @Profile("ollama")
    @Bean
    fun ollamaStreamingLlmModel(): StreamingChatLanguageModel {
        return LocalAiStreamingChatModel.builder()
            .modelName(chatModel)
            .baseUrl(llmChatBaseUrl)
            .maxTokens(2000)
            .temperature(0.2)
            .logResponses(true)
            .build()
    }

    @Bean
    fun assistant(streamingLlmModel: StreamingChatLanguageModel, store: ChatMemoryStore): Assistant {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }
        return AiServices.builder(Assistant::class.java)
            .streamingChatLanguageModel(streamingLlmModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build()
    }

    @Bean
    fun chatClassification(chatLanguageModel: ChatLanguageModel, store: ChatMemoryStore): ChatClassification {
        return AiServices.builder(ChatClassification::class.java)
            .chatLanguageModel(chatLanguageModel)
            .build()
    }

}