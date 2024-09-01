package agent

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatCommand
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatDocument
import com.mz.reservationsystem.aiagent.domain.chat.ChatApi
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary

@TestConfiguration
@ComponentScan("com.mz.reservationsystem.aiagent.domain.ai.**")
@ComponentScan("com.mz.reservationsystem.aiagent.adapter.llm.**")
class TestAiAgentConfiguration {

    @Primary
    @Bean
    fun chatMemoryStore(): ChatMemoryStore {
        return TestChatMemoryStore()
    }

    @Primary
    @Bean
    fun chatApi(): ChatApi {
        return TestChatApi()
    }

}

data class ChatData(val index: Int, val message: ChatMessage)

class TestChatMemoryStore : ChatMemoryStore {

    private val storage: MutableMap<Id, Set<ChatData>> = mutableMapOf()

    override fun getMessages(memoryId: Any): MutableList<ChatMessage> {
        val chatId = getChatId(memoryId)

        val messages = chatId
            ?.let { storage[it] }
            ?.toMutableList()
            ?.apply { sortBy { it.index } }

        return messages
            ?.map { it.message }
            ?.toMutableList()
            ?: emptyList<ChatMessage>().toMutableList()
    }

    override fun updateMessages(memoryId: Any, messages: List<ChatMessage>) {
        val chatId = getChatId(memoryId)!!

        val messagesToStore = chatId
            .let { storage[it] }
        val count = messagesToStore?.size ?: 0
        val messagesData = messages.mapIndexed { index, chatMessage -> ChatData(index + count, chatMessage) }
            .toSet()

        storage[chatId] = messagesToStore?.plus(messagesData) ?: messagesData
    }

    override fun deleteMessages(memoryId: Any) {
        TODO("Not yet implemented")
    }

    private fun getChatId(memoryId: Any): Id? = memoryId
        .let { it as? Id }
        ?: memoryId.let { it as? String }?.let { Id(it) }
}

class TestChatApi : ChatApi {
    override suspend fun execute(cmd: ChatCommand): ChatDocument {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Id): ChatDocument? {
        return null
    }

}