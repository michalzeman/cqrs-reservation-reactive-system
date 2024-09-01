package com.mz.reservationsystem.aiagent.domain.api.chat

import com.mz.ddd.common.api.domain.Id
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatCommandsApiTest {
    @Test
    fun `should create UpdateChatAgent command with given parameters`() {
        val aggregateId = Id("chatId")
        val chatAgentType = ChatAgentType.RESERVATION

        val command = UpdateChatAgent(aggregateId, chatAgentType)

        assertThat(command.aggregateId).isEqualTo(aggregateId)
        assertThat(command.chatAgentType).isEqualTo(chatAgentType)
    }

    @Test
    fun `should convert UpdateChatAgent command to ChatAgentChanged event`() {
        val aggregateId = Id("chatId")
        val chatAgentType = ChatAgentType.RESERVATION

        val command = UpdateChatAgent(aggregateId, chatAgentType)
        val event = command.toEvent()

        assertThat(event.aggregateId).isEqualTo(aggregateId)
        assertThat(event.chatAgentType).isEqualTo(chatAgentType)
    }
}
