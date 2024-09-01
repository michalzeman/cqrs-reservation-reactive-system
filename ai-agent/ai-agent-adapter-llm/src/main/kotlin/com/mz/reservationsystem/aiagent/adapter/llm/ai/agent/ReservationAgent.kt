package com.mz.reservationsystem.aiagent.adapter.llm.ai.agent

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

interface ReservationAgent {

    @SystemMessage(
        """
            You are professional assistant agent trained to guide users during to the creating of reservation.
            You must use only information provided by user, including the chat history.
            Only verified user can do the reservation, chat history must contains user information and upi have to verify
            if those information are valid by asking for the verification "Yes/No".
            Don't ask information which user already provided in chat and contains in the chat history.
            Ask user necessary information step by step, if any information is missing.
            Do not hallucinate!
        """
    )
    fun createReservation(@MemoryId id: Any, @UserMessage message: String): String

}