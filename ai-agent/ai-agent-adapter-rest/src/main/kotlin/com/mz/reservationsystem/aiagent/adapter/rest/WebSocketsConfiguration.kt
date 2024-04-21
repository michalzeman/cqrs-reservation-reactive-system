package com.mz.reservationsystem.aiagent.adapter.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping

@Configuration
class WebSocketsConfiguration {

    @Bean
    fun handlerMapping(aiAgentChatHandler: AiAgentChatHandler): HandlerMapping {
        val map = mapOf("/ai-agent/chat-stream" to aiAgentChatHandler)
        val order = -1 // before annotated controllers

        return SimpleUrlHandlerMapping(map, order)
    }
}