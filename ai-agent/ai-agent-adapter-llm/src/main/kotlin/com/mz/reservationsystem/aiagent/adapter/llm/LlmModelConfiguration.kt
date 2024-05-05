package com.mz.reservationsystem.aiagent.adapter.llm

import com.mz.reservationsystem.aiagent.domain.Assistant
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


@Configuration
class LlmModelConfiguration(@Value("\${adapter.llm.local.base-url}") private val llmBaseUrl: String) {

    @Bean
    fun localLlmModel(): ChatLanguageModel {
        return LocalAiChatModel.builder()
            .modelName("local-mistral")
            .baseUrl(llmBaseUrl)
            .maxTokens(100)
            .temperature(0.2)
            .build()
    }

    @Bean
    fun localStreamingLlmModel(): StreamingChatLanguageModel {
        return LocalAiStreamingChatModel.builder()
            .modelName("local-mistral")
            .baseUrl(llmBaseUrl)
            .maxTokens(2000)
            .temperature(0.2)
            .logResponses(true)
            .build()
    }

    @Bean
    fun assistant(localStreamingLlmModel: StreamingChatLanguageModel, store: ChatMemoryStore): Assistant {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }
        return AiServices.builder(Assistant::class.java)
            .streamingChatLanguageModel(localStreamingLlmModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build()
    }

}