package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

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
    fun chat(@MemoryId memoryId: Id, @UserMessage message: String): String

    @UserMessage(
        """
            Classify if conversion history contains Customer/User information loaded from the system. 
        """
    )
    fun isCustomerIdentified(@MemoryId memoryId: Id): Boolean

}