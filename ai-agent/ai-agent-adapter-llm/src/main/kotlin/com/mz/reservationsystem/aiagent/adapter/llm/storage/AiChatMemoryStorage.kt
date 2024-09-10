package com.mz.reservationsystem.aiagent.adapter.llm.storage

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.AddChatMessage
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAiMessage
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import com.mz.reservationsystem.aiagent.domain.api.chat.CreateChat
import com.mz.reservationsystem.aiagent.domain.chat.ChatApi
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ChatMessageDeserializer.messageFromJson
import dev.langchain4j.data.message.ChatMessageSerializer.messageToJson
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory


class AiChatMemoryStorage(private val chatApi: ChatApi) : ChatMemoryStore {

    companion object {
        private val logger = LogFactory.getLog(AiChatMemoryStorage::class.java)
    }

    override fun getMessages(memoryId: Any): List<ChatMessage> = runBlocking(Dispatchers.IO) {
        Result.runCatching {
            mapMemoryId(memoryId).let { id ->
                chatApi.findById(id)?.chatAiMessages?.map { it.toMessage() }
                    ?: createNewChat(id)
            }
        }.onFailure {
            logger.error(it)
        }.getOrElse {
            emptyList()
        }
    }

    override fun updateMessages(memoryId: Any, messages: List<ChatMessage>): Unit = runBlocking(Dispatchers.IO) {
        Result.runCatching {
            val jsonMessages = messages.map { messageToJson(it) }
            val chatAiMessages = jsonMessages.map { ChatAiMessage(Content(it)) }
            mapMemoryId(memoryId).let {
                chatApi.execute(AddChatMessage(it, chatAiMessages.toSet()))
            }
        }.onFailure { logger.error(it) }
    }

    override fun deleteMessages(memoryId: Any) {
        TODO("Not yet implemented")
    }

    private suspend fun createNewChat(id: Id): List<ChatMessage> {
        chatApi.execute(CreateChat(aggregateId = id))
        return emptyList()
    }

    private fun mapMemoryId(memoryId: Any): Id = when (memoryId) {
        is String -> Id(memoryId)
        else -> Id(memoryId.toString())
    }
}

internal fun ChatAiMessage.toMessage(): ChatMessage = messageFromJson(this.content.value)

