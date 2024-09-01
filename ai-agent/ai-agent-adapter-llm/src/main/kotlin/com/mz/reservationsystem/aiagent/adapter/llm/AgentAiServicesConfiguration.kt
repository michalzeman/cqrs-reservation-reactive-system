package com.mz.reservationsystem.aiagent.adapter.llm

import com.mz.reservationsystem.aiagent.adapter.llm.tools.Calculator
import com.mz.reservationsystem.aiagent.adapter.llm.tools.CustomerService
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.AssistantAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.ChatClassification
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.RegistrationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.ReservationAgent
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
    val calculator: Calculator,
    val customerService: CustomerService,
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
            .tools(customerService)
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
            .tools(calculator)
            .build()
    }

    @Bean
    fun chatClassification(chatLanguageModel: ChatLanguageModel): ChatClassification {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }

        return AiServices.builder(ChatClassification::class.java)
            .chatMemoryProvider(chatMemoryProvider)
            .chatLanguageModel(chatLanguageModel)
            .build()
    }
}