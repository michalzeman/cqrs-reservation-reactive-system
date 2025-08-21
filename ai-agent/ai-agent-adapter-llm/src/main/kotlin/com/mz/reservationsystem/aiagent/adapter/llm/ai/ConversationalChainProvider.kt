package com.mz.reservationsystem.aiagent.adapter.llm.ai

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.chain.ConversationalChain
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class ConversationalChainProvider(
    val store: ChatMemoryStore,
    @Qualifier("chatModel") val model: ChatModel
) {

    final inline fun <reified T> chatChain(chatId: Id): suspend (String) -> T {
        val json = Json { ignoreUnknownKeys = true }
        val chatMemory = MessageWindowChatMemory.builder()
            .id(chatId.value)
            .maxMessages(50)
            .chatMemoryStore(store)
            .build()

        val conversationalChain = ConversationalChain.builder()
            .chatMemory(chatMemory)
            .chatModel(model)
            .build()

        return { message: String ->
            val answer = conversationalChain.execute(message)
            if (T::class == String::class) answer as T
            else json.decodeFromString<T>(answer)
        }
    }
}
