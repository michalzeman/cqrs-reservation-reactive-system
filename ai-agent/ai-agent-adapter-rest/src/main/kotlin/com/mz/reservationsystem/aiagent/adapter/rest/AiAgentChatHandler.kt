package com.mz.reservationsystem.aiagent.adapter.rest

import com.mz.reservationsystem.aiagent.domain.agent.AgentManager
import com.mz.reservationsystem.aiagent.domain.agent.AgentRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


@Component
class AiAgentChatHandler(
    private val agentManager: AgentManager
) : WebSocketHandler {

    companion object {
        private val logger = LogFactory.getLog(AiAgentChatHandler::class.java)
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        return handleBetter(session)
    }

    fun handleBetter(session: WebSocketSession): Mono<Void> {
        val output = session.receive()
            .map { it.payloadAsText }
            .map { desJson<AgentRequest>(it) }
            .flatMap {
                agentManager.execute(it) { session.close().subscribe() }
            }
            .map { serToJsonString(it) }
            .map { session.textMessage(it) }
            .doOnError { logger.error(it) }
            .publishOn(Schedulers.boundedElastic())
        return session.send(output)
    }
}

/**
 * Kotlin native supported JSON serialization
 */
inline fun <reified T> serToJsonString(value: T) = Json.encodeToString<T>(value)

/**
 * Kotlin native supported JSON deserialization
 */
inline fun <reified T> desJson(value: String): T = Json.decodeFromString<T>(value)