package com.mz.reservationsystem.aiagent.adapter.llm

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.localai.LocalAiChatModel
import dev.langchain4j.model.localai.LocalAiStreamingChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("open-ai")
@Configuration
class OpenAiLlmModelConfiguration(
    @Value("\${adapter.llm.open-ai.api-key}") private val apiKey: String,
    @Value("\${adapter.llm.open-ai.model}") private val model: String
) {

    @Bean
    fun openAiChatModel(): ChatLanguageModel {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(model)
            .temperature(0.1)
            .build()
    }

    @Bean
    fun openAiStreamingChatModel(): StreamingChatLanguageModel {
        return OpenAiStreamingChatModel.builder()
            .apiKey(apiKey)
            .modelName(model)
            .temperature(0.1)
            .build()
    }

}