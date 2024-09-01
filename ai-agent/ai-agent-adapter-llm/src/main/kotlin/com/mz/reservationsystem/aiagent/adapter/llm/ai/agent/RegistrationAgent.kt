package com.mz.reservationsystem.aiagent.adapter.llm.ai.agent

import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.V

interface RegistrationAgent {

    @SystemMessage(
        """
         You are a professional trained customer agent, to train for the CUSTOMER REGISTRATION PROCESS.
         You are communicating with a customer via online chat.
         You must use information only provided by the user.
         You must use messages history for the user already provided information. 
         Ask User step by step information needed for the registration.
         Information needed to register new customer are:
         - first name
         - last name
         - email
         When registration is done you must provide id to customer.
        """
    )
    fun chat(@MemoryId memoryId: Any, @UserMessage message: String): String

}