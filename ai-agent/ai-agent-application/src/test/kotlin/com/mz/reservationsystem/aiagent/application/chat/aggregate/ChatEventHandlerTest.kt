package com.mz.reservationsystem.aiagent.application.chat.aggregate

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.reservationsystem.aiagent.domain.api.chat.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChatEventHandlerTest {

    private lateinit var chatEventHandler: ChatEventHandler

    @BeforeEach
    fun setUp() {
        chatEventHandler = ChatEventHandler()
    }

    @Test
    fun `apply, empty chat, chat created event`() {
        val emptyChat = EmptyChat()
        val chatId = Id("1")
        val chatCreated = ChatCreated(chatId)

        val result = chatEventHandler.apply(emptyChat, chatCreated)

        assertThat(result).isInstanceOf(Chat::class.java)
        // Add more assertions based on your expected result
    }

    @Test
    fun `apply, empty chat, chat message added event`() {
        val emptyChat = EmptyChat()
        val chatId = Id("1")
        val chatMessage = ChatAiMessage(Content("Hello"))
        val wrongEvent = ChatMessageAdded(chatId, setOf(chatMessage))

        val result = chatEventHandler.apply(emptyChat, wrongEvent)

        assertThat(result).isInstanceOf(UnknownCustomerChat::class.java)
        assertThat((result as UnknownCustomerChat).chatAiMessages).containsExactly(chatMessage)
    }

    @Test
    fun `apply, empty chat, customer id added event`() {
        val emptyChat = EmptyChat()
        val chatId = Id("1")
        val customerId = Id("123")
        val customerIdAdded = CustomerIdAdded(chatId, customerId)

        val result = chatEventHandler.apply(emptyChat, customerIdAdded)

        assertThat(result).isInstanceOf(CustomerChat::class.java)
        assertThat((result as CustomerChat).customerId).isEqualTo(customerId)
    }

    @Test
fun `apply, unknown customer chat, chat message added event`() {
    val chatId = Id("1")
    val chat = UnknownCustomerChat(chatId, Version(0), emptySet())
    val chatMessage = ChatAiMessage(Content("Hello"))
    val chatMessageAdded = ChatMessageAdded(chatId, setOf(chatMessage))

    val result = chatEventHandler.apply(chat, chatMessageAdded)

    assertThat(result).isInstanceOf(UnknownCustomerChat::class.java)
    assertThat((result as UnknownCustomerChat).chatAiMessages).containsExactly(chatMessage)
}

@Test
fun `apply, unknown customer chat, customer id added event`() {
    val chatId = Id("1")
    val chat = UnknownCustomerChat(chatId, Version(0), emptySet())
    val customerId = Id("123")
    val customerIdAdded = CustomerIdAdded(chatId, customerId)

    val result = chatEventHandler.apply(chat, customerIdAdded)

    assertThat(result).isInstanceOf(CustomerChat::class.java)
    assertThat((result as CustomerChat).customerId).isEqualTo(customerId)
}

@Test
fun `apply, customer chat, chat message added event`() {
    val chatId = Id("1")
    val customerId = Id("123")
    val chat = CustomerChat(chatId, Version(0), customerId, emptySet())
    val chatMessage = ChatAiMessage(Content("Hello"))
    val chatMessageAdded = ChatMessageAdded(chatId, setOf(chatMessage))

    val result = chatEventHandler.apply(chat, chatMessageAdded)

    assertThat(result).isInstanceOf(CustomerChat::class.java)
    assertThat((result as CustomerChat).chatAiMessages).containsExactly(chatMessage)
}

@Test
fun `apply, unknown customer chat, wrong event type`() {
    val chatId = Id("1")
    val chat = UnknownCustomerChat(chatId, Version(0), emptySet())
    val wrongEvent = ChatCreated(chatId)

    val exception = assertThrows<RuntimeException> {
        chatEventHandler.apply(chat, wrongEvent)
    }

    assertThat(exception.message).isEqualTo("Wrong event type ${wrongEvent::class} for the existing chat aggregate")
}

@Test
fun `apply, customer chat, wrong event type`() {
    val chatId = Id("1")
    val customerId = Id("123")
    val chat = CustomerChat(chatId, Version(0), customerId, emptySet())
    val wrongEvent = ChatCreated(chatId)

    val exception = assertThrows<RuntimeException> {
        chatEventHandler.apply(chat, wrongEvent)
    }

    assertThat(exception.message).isEqualTo("Wrong event type ${wrongEvent::class} for the customer chat aggregate")
}
}