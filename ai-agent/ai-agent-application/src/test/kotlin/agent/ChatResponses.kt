package agent

import com.mz.reservationsystem.aiagent.domain.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.api.chat.Content

fun aggregateAgentResponseFlow(): suspend (accumulator: ChatResponse, value: ChatResponse) -> ChatResponse =
    { accumulator, value ->
        accumulator.copy(message = Content("${accumulator.message.value} ${value.message.value}"))
    }