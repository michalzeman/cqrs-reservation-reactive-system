package com.mz.reservationsystem.aiagent.adapter.rest

import com.mz.common.components.json.desJson
import com.mz.common.components.json.serToJsonString
import com.mz.reservationsystem.aiagent.application.ai.AgentManager
import com.mz.reservationsystem.aiagent.application.ai.model.AgentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
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
            .flatMap { agentRequest ->
                agentManager.execute(agentRequest)
                    .map { serToJsonString(it) }
                    .map { session.textMessage(it) }
                    .onCompletion { session.close().subscribe() }
                    .asFlux(Dispatchers.IO)
            }
            .doOnError {
                logger.error(it.message, it)
            }
            .publishOn(Schedulers.boundedElastic())

        return session.send(output)
    }
}
