package com.mz.reservationsystem.aiagent.adapter.rest

import com.mz.common.components.json.desJson
import com.mz.common.components.json.serToJsonString
import com.mz.reservationsystem.aiagent.domain.ai.AgentManager
import com.mz.reservationsystem.aiagent.domain.ai.model.AgentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.asFlux
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
        val output = session.receive()
            .map { it.payloadAsText }
            .map { desJson<AgentRequest>(it) }
            .flatMap {
                agentManager.execute(it) { session.close().subscribe() }
                    .asFlux(Dispatchers.IO)
            }
            .map { serToJsonString(it) }
            .map { session.textMessage(it) }
            .doOnError { logger.error(it) }
            .publishOn(Schedulers.boundedElastic())

        return session.send(output)
    }
}
