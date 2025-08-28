package com.mz.reservationsystem.aiagent.application.chat.aggregate

import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.reservationsystem.aiagent.domain.api.chat.*

class ChatCommandHandler : AggregateCommandHandler<Chat, ChatCommand, ChatEvent> {

    override fun execute(aggregate: Chat, command: ChatCommand): Result<List<ChatEvent>> {
        return when (aggregate) {
            is EmptyChat -> aggregate.newChat(command)
            is UnknownCustomerChat -> aggregate.existingChat(command)
            is CustomerChat -> aggregate.customerChat(command)
        }
    }

    private fun Chat.newChat(cmd: ChatCommand): Result<List<ChatEvent>> = Result.runCatching {
        when (cmd) {
            is CreateChat -> listOf(cmd.toEvent())
            is UpdateChatAgent -> handler(cmd)
            is AddChatMessage -> listOf(cmd.toEvent())
            is AddCustomerId -> listOf(cmd.toEvent())
        }
    }

    private fun Chat.existingChat(cmd: ChatCommand): Result<List<ChatEvent>> = Result.runCatching {
        when (cmd) {
            is CreateChat -> throw RuntimeException("Cannot apply command: ${cmd::class} on existing aggregate")
            is AddChatMessage -> listOf(cmd.toEvent())
            is AddCustomerId -> listOf(cmd.toEvent())
            is UpdateChatAgent -> handler(cmd)
        }
    }

    private fun Chat.customerChat(cmd: ChatCommand): Result<List<ChatEvent>> = Result.runCatching {
        when (cmd) {
            is CreateChat -> throw RuntimeException("Cannot apply command: ${cmd::class} on existing aggregate")
            is AddCustomerId -> throw RuntimeException("Cannot apply command: ${cmd::class} on existing aggregate")
            is AddChatMessage -> listOf(cmd.toEvent())
            is UpdateChatAgent -> handler(cmd)
        }
    }

    private fun Chat.handler(cmd: UpdateChatAgent): List<ChatEvent> =
        if (chatAgentType == cmd.chatAgentType) emptyList()
        else listOf(cmd.toEvent())
}