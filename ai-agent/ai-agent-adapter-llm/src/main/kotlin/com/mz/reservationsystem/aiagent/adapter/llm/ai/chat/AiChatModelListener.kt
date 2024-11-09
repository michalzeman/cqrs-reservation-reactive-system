package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat

import dev.langchain4j.model.chat.listener.ChatModelErrorContext
import dev.langchain4j.model.chat.listener.ChatModelListener
import dev.langchain4j.model.chat.listener.ChatModelRequestContext
import dev.langchain4j.model.chat.listener.ChatModelResponseContext
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

private val logger = LogFactory.getLog(AiChatModelListener::class.java)

@Component
class AiChatModelListener : ChatModelListener {
    override fun onRequest(requestContext: ChatModelRequestContext) {
        logger.debug("Request -> ${requestContext.request().messages()}")
    }

    override fun onResponse(responseContext: ChatModelResponseContext) {
        logger.info("Response -> ${responseContext.response().aiMessage()}")
    }

    override fun onError(errorContext: ChatModelErrorContext) {
        logger.error("Error -> ${errorContext.error()}")
    }
}