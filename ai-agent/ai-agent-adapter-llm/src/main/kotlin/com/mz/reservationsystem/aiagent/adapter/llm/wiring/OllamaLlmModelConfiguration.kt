package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.AiChatModelListener
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("ollama")
@Configuration
@EnableConfigurationProperties(LlmChatModelProperties::class)
class OllamaLlmModelConfiguration(
    val properties: LlmChatModelProperties,
    val aiChatModelListener: AiChatModelListener,
) {

    @Bean
    fun ollamaLlmModel(): ChatLanguageModel {
        return OllamaChatModel.builder()
            .modelName(properties.model)
            .maxRetries(5)
            .logRequests(true)
            .logResponses(true)
            .baseUrl(properties.baseUrl)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .build()
    }

    @Bean
    fun ollamaStreamingLlmModel(): StreamingChatLanguageModel {
        return OllamaStreamingChatModel.builder()
            .modelName(properties.model)
            .baseUrl(properties.baseUrl)
//            .logRequests(true)
//            .logResponses(true)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .build()
    }

}