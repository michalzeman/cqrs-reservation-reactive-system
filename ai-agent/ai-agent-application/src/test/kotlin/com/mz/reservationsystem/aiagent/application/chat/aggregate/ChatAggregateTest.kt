package com.mz.reservationsystem.aiagent.application.chat.aggregate

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.domain.api.chat.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatAggregateTest {
    @Test
    fun `create new Chat`() {
        // Given
        val chatId = Id("1")
        val chat = EmptyChat(chatId)
        val chatCreated = ChatCreated(chatId)

        // When
        val newChat = chat.apply(chatCreated)

        // Then
        assertThat(newChat).isInstanceOf(UnknownCustomerChat::class.java)
        assertThat(newChat.aggregateId).isEqualTo(chatId)
        assertThat(newChat.version).isEqualTo(Version(0))
    }

    @Test
    fun `add chat message to unknown customer chat`() {
        // Given
        val chatId = Id("1")
        val chat = UnknownCustomerChat(chatId, Version(0), emptySet())
        val chatMessage = ChatAiMessage(Content("Hello"))
        val chatMessageAdded = ChatMessageAdded(chatId, setOf(chatMessage))

        // When
        val updatedChat = chat.apply(chatMessageAdded)

        // Then
        assertThat(updatedChat).isInstanceOf(UnknownCustomerChat::class.java)
        assertThat(updatedChat.aggregateId).isEqualTo(chatId)
        assertThat(updatedChat.version).isEqualTo(Version(1))
        assertThat(updatedChat.chatAiMessages).containsExactly(chatMessage)
    }

    @Test
    fun `add customer id to unknown customer chat`() {
        // Given
        val chatId = Id("1")
        val chat = UnknownCustomerChat(chatId, Version(0), emptySet())
        val customerId = Id("123")
        val customerIdAdded = CustomerIdAdded(chatId, customerId)

        // When
        val updatedChat = chat.apply(customerIdAdded)

        // Then
        assertThat(updatedChat).isInstanceOf(CustomerChat::class.java)
        assertThat(updatedChat.aggregateId).isEqualTo(chatId)
        assertThat(updatedChat.version).isEqualTo(Version(1))
        assertThat(updatedChat.customerId).isEqualTo(customerId)
    }

    @Test
    fun `should return EmptyChat for NEW_CHAT_ID`() {
        val id = NEW_CHAT_ID
        val result = id.getAggregate()

        assertThat(result).isInstanceOf(EmptyChat::class.java)
        assertThat(result.aggregateId).isNotEqualTo(NEW_CHAT_ID)
        assertThat(result.version).isEqualTo(Version(0))
    }

    @Test
    fun `should return EmptyChat for any other ID`() {
        val id = Id("anyOtherId")
        val result = id.getAggregate()

        assertThat(result).isInstanceOf(EmptyChat::class.java)
        assertThat(result.aggregateId).isEqualTo(id)
        assertThat(result.version).isEqualTo(Version(0))
    }

    @Test
    fun `apply ChatAgentChanged event to EmptyChat updates version and chatAgentType`() {
        val chat = EmptyChat()
        val event = ChatAgentChanged(newId(), ChatAgentType.RESERVATION_VIEW)

        val updatedChat = chat.apply(event)

        assertThat(updatedChat.version).isEqualTo(Version(1))
        assertThat(updatedChat.chatAgentType).isEqualTo(ChatAgentType.RESERVATION_VIEW)
    }

    @Test
    fun `apply ChatAgentChanged event to UnknownCustomerChat updates version and chatAgentType`() {
        val aggregateId = Id("test")
        val chat = UnknownCustomerChat(aggregateId = aggregateId, version = Version(1), chatAiMessages = emptySet())
        val event = ChatAgentChanged(aggregateId, ChatAgentType.RESERVATION)

        val updatedChat = chat.apply(event)

        assertThat(updatedChat.version).isEqualTo(Version(2))
        assertThat(updatedChat.chatAgentType).isEqualTo(ChatAgentType.RESERVATION)
    }

    @Test
    fun `apply ChatAgentChanged event to CustomerChat updates version and chatAgentType`() {
        val aggregateId = Id("test")
        val chat = CustomerChat(
            aggregateId = aggregateId,
            version = Version(1),
            customerId = Id("customer"),
            chatAiMessages = emptySet()
        )
        val event = ChatAgentChanged(aggregateId, ChatAgentType.USER_REGISTRATION)

        val updatedChat = chat.apply(event)

        assertThat(updatedChat.version).isEqualTo(Version(2))
        assertThat(updatedChat.chatAgentType).isEqualTo(ChatAgentType.USER_REGISTRATION)
    }
}
