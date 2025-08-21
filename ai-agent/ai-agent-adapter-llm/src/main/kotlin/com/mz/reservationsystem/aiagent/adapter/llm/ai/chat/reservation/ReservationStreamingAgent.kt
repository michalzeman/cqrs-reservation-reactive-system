package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import reactor.core.publisher.Flux

interface ReservationStreamingAgent {

    @SystemMessage(
        """
            You are professional assistant agent trained to guide customer during to the creating of reservation.
            You must use only information provided by customer, including the chat history.
            Only verified customer can do the reservation, chat history must contains customer information, and 
            you have to verify if those information are valid by asking for the verification "Yes/No".           
            Don't ask information which customer already provided in chat and contains in the chat history.
            Ask customer necessary information step by step, if any information is missing.
            Do not hallucinate!
            
            Information needed to create reservation are:
            - Customer must be verified by Customer id.
            - for the reservation customer hast to provide date and start time and end time of the reservation.
            - for the given date and time there must be available time slot
        """
    )
    fun createReservation(@MemoryId id: Id, @UserMessage message: String): Flux<String>

}