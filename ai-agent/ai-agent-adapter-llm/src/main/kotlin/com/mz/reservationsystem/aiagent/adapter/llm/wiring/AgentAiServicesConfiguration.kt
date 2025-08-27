package com.mz.reservationsystem.aiagent.adapter.llm.wiring

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.AssistantAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.ChatClassification
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerIdentificationTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationTool
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AgentAiServicesConfiguration(
    val customerTool: CustomerTool,
    val customerIdentificationTool: CustomerIdentificationTool,
    val reservationTool: ReservationTool,
    val store: ChatMemoryStore
) {
    @Bean
    fun assistant(streamingLlmModel: StreamingChatModel): AssistantAgent {

        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }
        return AiServices.builder(AssistantAgent::class.java)
            .streamingChatModel(streamingLlmModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build()
    }

    @Bean
    fun customerAgent(
        chatLanguageModel: StreamingChatModel
    ): CustomerAgent {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }

        return AiServices.builder(CustomerAgent::class.java)
            .streamingChatModel(chatLanguageModel)
            .chatMemoryProvider(chatMemoryProvider)
            .tools(customerTool, customerIdentificationTool)
            .build()
    }

    @Bean
    fun reservationAgent(
        chatLanguageModel: StreamingChatModel
    ): ReservationAgent {
        val chatMemoryProvider = ChatMemoryProvider { memoryId: Any? ->
            MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(50)
                .chatMemoryStore(store)
                .build()
        }

        chatLanguageModel.defaultRequestParameters()

        return AiServices.builder(ReservationAgent::class.java)
            .chatMemoryProvider(chatMemoryProvider)
            .streamingChatModel(chatLanguageModel)
            .tools(reservationTool, customerIdentificationTool)
            .build()
    }

    @Bean
    fun chatClassification(@Qualifier("smallModel") chatLanguageModel: ChatModel): ChatClassification {
        return AiServices.builder(ChatClassification::class.java)
            .chatModel(chatLanguageModel)
            .build()
    }
}