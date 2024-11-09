package com.mz.reservationsystem.aiagent.adapter.llm.storage

import com.mz.reservationsystem.aiagent.domain.chat.ChatApi
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiChatMemoryStorageConfiguration {

    @Bean
    @ConditionalOnBean(ChatApi::class)
    fun llmChatMemoryStore(chatApi: ChatApi): ChatMemoryStore {
        return AiChatMemoryStorage(chatApi)
    }

}