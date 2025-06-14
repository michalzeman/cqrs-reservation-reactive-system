package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.AiChatModelListener
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("open-ai")
@Configuration
@EnableConfigurationProperties(LlmChatModelProperties::class)
class OpenAiLlmModelConfiguration(
    @Value("\${adapter.llm.open-ai.api-key}") private val apiKey: String,
    private val properties: LlmChatModelProperties,
    val aiChatModelListener: AiChatModelListener,
) {

    @Bean
    fun openAiChatModel(): ChatModel {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(properties.model)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .build()
    }

    @Bean
    fun openAiStreamingChatModel(): StreamingChatModel {
        return OpenAiStreamingChatModel.builder()
            .apiKey(apiKey)
            .modelName(properties.model)
            .temperature(properties.temperature)
            .listeners(listOf(aiChatModelListener))
            .build()
    }

}