package com.mz.reservationsystem.aiagent.domain.chat.aggregate

import com.mz.reservationsystem.aiagent.domain.api.chat.*
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler

class ChatCommandHandler : AggregateCommandHandler<Chat, ChatCommand, ChatEvent> {

    override fun execute(aggregate: Chat, command: ChatCommand): Result<List<ChatEvent>> {
        return when (aggregate) {
            is EmptyChat -> newChat(command)
            is UnknownCustomerChat -> existingChat(command)
            is CustomerChat -> customerChat(command)
        }
    }

    private fun newChat(cmd: ChatCommand): Result<List<ChatEvent>> {
        return Result.runCatching {
            when (cmd) {
                is CreateChat -> listOf(cmd.toEvent())
                else -> throw RuntimeException("Cannot apply command: ${cmd::class} on non existing aggregate")
            }
        }
    }

    private fun existingChat(cmd: ChatCommand): Result<List<ChatEvent>> {
        return Result.runCatching {
            when (cmd) {
                is CreateChat -> throw RuntimeException("Cannot apply command: ${cmd::class} on existing aggregate")
                is AddChatMessage -> listOf(cmd.toEvent())
                is AddCustomerId -> listOf(cmd.toEvent())
            }
        }
    }

    private fun customerChat(cmd: ChatCommand): Result<List<ChatEvent>> {
        return Result.runCatching {
            when (cmd) {
                is CreateChat -> throw RuntimeException("Cannot apply command: ${cmd::class} on existing aggregate")
                is AddCustomerId -> throw RuntimeException("Cannot apply command: ${cmd::class} on existing aggregate")
                is AddChatMessage -> listOf(cmd.toEvent())
            }
        }
    }
}