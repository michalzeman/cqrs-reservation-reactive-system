package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import reactor.core.publisher.Flux

interface ReservationAgent {

    @SystemMessage(fromResource = "prompts/reservation-agent-create-reservation-chat.txt")
//    fun createReservation(@MemoryId id: Id, @UserMessage message: String): String
    fun createReservation(@MemoryId id: Id, @UserMessage message: String): Flux<String>

    @SystemMessage(fromResource = "prompts/reservation-agent-list-customer-reservation-chat.txt")
//    fun listAllCustomerReservation(@MemoryId id: Id, @UserMessage message: String): String
    fun listAllCustomerReservation(@MemoryId id: Id, @UserMessage message: String): Flux<String>
}