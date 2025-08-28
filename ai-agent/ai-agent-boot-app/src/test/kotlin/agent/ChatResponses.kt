package agent

import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.aiagent.application.ai.model.AgentRequest
import com.mz.reservationsystem.aiagent.application.ai.model.AgentResponse
import com.mz.reservationsystem.aiagent.application.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce

fun aggregateAgentResponseFlow(): suspend (accumulator: ChatResponse, value: ChatResponse) -> ChatResponse =
    { accumulator, value ->
        accumulator.copy(message = Content("${accumulator.message.value}${value.message.value}"))
    }

fun buildTestChat(aiChat: (AgentRequest) -> Flow<AgentResponse>): suspend (AgentRequest) -> Id {
    return {
        request ->
        println("User: ${request.message.value}")
        println("Agent:")
        val result = aiChat(request).map { it as ChatResponse }
            .onEach { print(it.message.value) }
            .reduce(aggregateAgentResponseFlow())

        result.chatId
    }
}