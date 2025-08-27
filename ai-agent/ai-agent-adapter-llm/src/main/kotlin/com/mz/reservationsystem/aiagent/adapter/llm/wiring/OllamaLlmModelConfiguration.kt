package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.AiChatModelListener
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.chat.request.ResponseFormat
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

    @Bean("chatModel")
    fun ollamaLlmModel(): ChatModel {
        return OllamaChatModel.builder()
            .modelName(properties.model)
            .maxRetries(5)
            .baseUrl(properties.baseUrl)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .build()
    }

    @Bean("smallModel")
    fun ollamaLlmSmallModel(): ChatModel {
        return OllamaChatModel.builder()
            .modelName(properties.smallModel)
            .maxRetries(5)
            .baseUrl(properties.baseUrl)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .build()
    }

    @Bean
    fun ollamaStreamingLlmModel(): StreamingChatModel {
        return OllamaStreamingChatModel.builder()
            .modelName(properties.model)
            .baseUrl(properties.baseUrl)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .think(false)
//            .responseFormat(ResponseFormat.JSON)
            .build()
    }

}