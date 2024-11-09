package com.mz.reservationsystem.aiagent.adapter.llm

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.AssistantAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.ChatClassification
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationStreamingAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.RegistrationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationTool
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AgentAiServicesConfiguration(
    val customerTool: CustomerTool,
    val reservationTool: ReservationTool,
    val store: ChatMemoryStore
) {
    @Bean
    fun assistant(streamingLlmModel: StreamingChatLanguageModel): AssistantAgent {

        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }
        return AiServices.builder(AssistantAgent::class.java)
            .streamingChatLanguageModel(streamingLlmModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build()
    }

    @Bean
    fun registrationAgent(chatLanguageModel: ChatLanguageModel): RegistrationAgent {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }

        return AiServices.builder(RegistrationAgent::class.java)
            .chatLanguageModel(chatLanguageModel)
            .chatMemoryProvider(chatMemoryProvider)
            .tools(customerTool)
            .build()
    }

    @Bean
    fun reservationAgent(chatLanguageModel: ChatLanguageModel): ReservationAgent {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }

        return AiServices.builder(ReservationAgent::class.java)
            .chatMemoryProvider(chatMemoryProvider)
            .chatLanguageModel(chatLanguageModel)
            .tools(reservationTool, customerTool)
            .build()
    }

    @Bean
    fun reservationStreamingAgent(chatLanguageModel: StreamingChatLanguageModel): ReservationStreamingAgent {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }

        return AiServices.builder(ReservationStreamingAgent::class.java)
            .chatMemoryProvider(chatMemoryProvider)
            .streamingChatLanguageModel(chatLanguageModel)
            .tools(reservationTool, customerTool)
            .build()
    }

    @Bean
    fun chatClassification(chatLanguageModel: ChatLanguageModel): ChatClassification {
        return AiServices.builder(ChatClassification::class.java)
            .chatLanguageModel(chatLanguageModel)
            .build()
    }
}