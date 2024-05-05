package com.mz.reservationsystem.aiagent.domain.chat.aggregate

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.reservationsystem.aiagent.domain.api.chat.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatCommandHandlerTest {

    private lateinit var chatCommandHandler: ChatCommandHandler

    @BeforeEach
    fun setUp() {
        chatCommandHandler = ChatCommandHandler()
    }

    @Test
    fun `should handle new chat command`() {
        val command = CreateChat(Id("chatId"))
        val result = chatCommandHandler.execute(EmptyChat(Id("chatId")), command)

        val event = result.getOrThrow()[0]

        assertThat(event).isInstanceOf(ChatCreated::class.java)
        assertThat(event.aggregateId).isEqualTo(Id("chatId"))
    }

    @Test
    fun `should not handle new chat command for existing chat`() {
        val command = CreateChat(Id("chatId"))

        val result = chatCommandHandler.execute(UnknownCustomerChat(Id("chatId"), Version(0), emptySet()), command)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should handle add chat message command for existing chat`() {
        val command = AddChatMessage(Id("chatId"), setOf(ChatAiMessage(Content("message"))))
        val result = chatCommandHandler.execute(UnknownCustomerChat(Id("chatId"), Version(0), emptySet()), command)

        val event = result.getOrThrow()[0]

        assertThat(event).isInstanceOf(ChatMessageAdded::class.java)
        assertThat(event.aggregateId).isEqualTo(Id("chatId"))
        assertThat((event as ChatMessageAdded).chatAiMessages.first()).isEqualTo(ChatAiMessage(Content("message")))
    }

    @Test
    fun `should handle add customer id command for existing chat`() {
        val command = AddCustomerId(Id("chatId"), Id("customerId"))
        val result = chatCommandHandler.execute(UnknownCustomerChat(Id("chatId"), Version(0), emptySet()), command)

        val event = result.getOrThrow()[0]
        assertThat(event).isInstanceOf(CustomerIdAdded::class.java)
        assertThat(event.aggregateId).isEqualTo(Id("chatId"))
        assertThat((event as CustomerIdAdded).customerId).isEqualTo(Id("customerId"))
    }

    @Test
    fun `should not handle add customer id command for customer chat`() {
        val command = AddCustomerId(Id("chatId"), Id("customerId"))

        val result = chatCommandHandler.execute(CustomerChat(Id("chatId"), Version(0), Id("customerId"), emptySet()), command)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should handle add chat message command for customer chat`() {
        val command = AddChatMessage(Id("chatId"), setOf(ChatAiMessage(Content("message"))))
        val result = chatCommandHandler.execute(CustomerChat(Id("chatId"), Version(0), Id("customerId"), emptySet()), command)

        val event = result.getOrThrow()[0]
        assertThat(event).isInstanceOf(ChatMessageAdded::class.java)
        assertThat(event.aggregateId).isEqualTo(Id("chatId"))
        assertThat((event as ChatMessageAdded).chatAiMessages.first()).isEqualTo(ChatAiMessage(Content("message")))
    }
}