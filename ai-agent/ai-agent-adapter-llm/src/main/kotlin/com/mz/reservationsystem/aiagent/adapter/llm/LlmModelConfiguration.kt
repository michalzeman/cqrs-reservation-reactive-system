package com.mz.reservationsystem.aiagent.adapter.llm

import com.mz.reservationsystem.aiagent.model.Assistant
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.localai.LocalAiChatModel
import dev.langchain4j.model.localai.LocalAiStreamingChatModel
import dev.langchain4j.service.AiServices
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LlmModelConfiguration {

    @Bean
    fun localLlmModel(): ChatLanguageModel {
        return LocalAiChatModel.builder()
            .modelName("local-mistral")
            .baseUrl("http://localhost:1234/v1")
            .maxTokens(100)
//            .temperature(0.2)
            .temperature(2.0)
            .build()
    }

    @Bean
    fun localStreamingLlmModel(): StreamingChatLanguageModel {
        return LocalAiStreamingChatModel.builder()
            .modelName("local-mistral")
            .baseUrl("http://localhost:1234/v1")
            .maxTokens(100)
            .temperature(0.2)
            .logResponses(true)
            .build()
    }

    @Bean
    fun assistant(localStreamingLlmModel: StreamingChatLanguageModel): Assistant {
        return AiServices.create(Assistant::class.java, localStreamingLlmModel)
    }

}