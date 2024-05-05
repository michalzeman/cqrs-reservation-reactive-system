package com.mz.reservationsystem.aiagent.domain.chat.aggregate

import com.mz.ddd.common.api.domain.*
import com.mz.reservationsystem.aiagent.domain.api.chat.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val CHAT_DOMAIN_TAG = DomainTag("chat")

@Serializable
sealed class Chat : Aggregate() {
    abstract val version: Version
}

fun Id.getAggregate(): Chat {
    return when (this) {
        NEW_CHAT_ID -> EmptyChat(newId())
        else -> EmptyChat(this)
    }
}

fun Chat.toDocument(events: Set<ChatEvent> = emptySet()): ChatDocument {
    return when (this) {
        is EmptyChat -> error("Chat is not created yet")
        is UnknownCustomerChat -> ChatDocument(
            chatAiMessages = chatAiMessages,
            version = version,
            aggregateId = aggregateId,
            events = events
        )
        is CustomerChat -> ChatDocument(
            customerId = customerId,
            version = version,
            aggregateId = aggregateId,
            events = events
        )
    }
}

@Serializable
@SerialName("empty-chat")
data class EmptyChat(
    override val aggregateId: Id = NEW_CHAT_ID,
    override val version: Version = Version(0)
) : Chat()

fun EmptyChat.apply(event: ChatCreated): UnknownCustomerChat = UnknownCustomerChat(
    aggregateId = event.aggregateId,
    version = this.version,
    chatAiMessages = emptySet()
)

@Serializable
@SerialName("unknown-customer-chat")
data class UnknownCustomerChat(
    override val aggregateId: Id,
    override val version: Version,
    val chatAiMessages: Set<ChatAiMessage>
) : Chat()

fun UnknownCustomerChat.apply(event: ChatMessageAdded): UnknownCustomerChat = this.copy(
    chatAiMessages = chatAiMessages + event.chatAiMessages,
    version = version.increment()
)

fun UnknownCustomerChat.apply(event: CustomerIdAdded): CustomerChat = CustomerChat(
    aggregateId = aggregateId,
    version = version.increment(),
    customerId = event.customerId,
    chatAiMessages = this.chatAiMessages
)

@Serializable
@SerialName("customer-chat")
data class CustomerChat(
    override val aggregateId: Id,
    override val version: Version,
    val customerId: Id,
    val chatAiMessages: Set<ChatAiMessage>
) : Chat()

fun CustomerChat.apply(event: ChatMessageAdded): CustomerChat = this.copy(
    chatAiMessages = chatAiMessages + event.chatAiMessages,
    version = version.increment(),
)
