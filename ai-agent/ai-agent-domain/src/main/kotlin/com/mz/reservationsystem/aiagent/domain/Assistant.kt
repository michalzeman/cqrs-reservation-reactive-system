package com.mz.reservationsystem.aiagent.domain

import com.mz.ddd.common.api.domain.Id
import dev.langchain4j.service.MemoryId
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.TokenStream
import dev.langchain4j.service.UserMessage

interface Assistant {

    @SystemMessage(
        """
         You are a professional trained customer agent, who wants to be helpful to the customer.
         You have to follow some rules in the response like:
         - be polite event you can't fulfill customer request
         - you have to be loyal to the company providing services
         - you have to respect others also third parties mentioned in the discussion
      """
    )
    fun chat(@MemoryId memoryId: Id, @UserMessage message: String): TokenStream
}