package com.mz.reservationsystem.aiagent.domain.chat.aggregate

import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.reservationsystem.aiagent.domain.api.chat.*

class ChatEventHandler : AggregateEventHandler<Chat, ChatEvent> {

    override fun apply(aggregate: Chat, event: ChatEvent): Chat = when (aggregate) {
        is EmptyChat -> newChat(aggregate, event)
        is UnknownCustomerChat -> existingChat(aggregate, event)
        is CustomerChat -> customerChat(aggregate, event)
    }

    private fun newChat(aggregate: EmptyChat, event: ChatEvent): Chat = when (event) {
        is ChatCreated -> aggregate.apply(event)
        is ChatAgentChanged -> aggregate.apply(event)
        is ChatMessageAdded, is CustomerIdAdded -> throw RuntimeException("Wrong event type $event for the empty chat aggregate")
    }

    private fun existingChat(aggregate: UnknownCustomerChat, event: ChatEvent): Chat = when (event) {
        is ChatCreated -> throw RuntimeException("Wrong event type ${event::class} for the existing chat aggregate")
        is ChatMessageAdded -> aggregate.apply(event)
        is CustomerIdAdded -> aggregate.apply(event)
        is ChatAgentChanged -> aggregate.apply(event)
    }

    private fun customerChat(aggregate: CustomerChat, event: ChatEvent): Chat = when (event) {
        is CustomerIdAdded, is ChatCreated -> throw RuntimeException("Wrong event type ${event::class} for the customer chat aggregate")
        is ChatMessageAdded -> aggregate.apply(event)
        is ChatAgentChanged -> aggregate.apply(event)
    }
}
