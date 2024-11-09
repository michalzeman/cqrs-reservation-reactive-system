package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

interface ReservationAgent {

    @SystemMessage(fromResource = "prompts/reservation-agent-create-reservation-sys.txt")
    fun createReservation(@MemoryId id: Id, @UserMessage message: String): String
}