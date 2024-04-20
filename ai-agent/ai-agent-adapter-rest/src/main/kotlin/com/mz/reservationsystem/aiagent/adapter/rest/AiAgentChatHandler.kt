package com.mz.reservationsystem.aiagent.adapter.rest

import com.mz.reservationsystem.aiagent.model.Assistant
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Component
class AiAgentChatHandler(
    private val assistant: Assistant
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        return handleBetter(session)
    }

    fun handleBetter(session: WebSocketSession): Mono<Void> {
        val source = session.receive()
            .map { it.payloadAsText }
            .flatMap {
                chat(it)
            }
        return session.send(source.map { session.textMessage(it) })
    }

    fun chat(message: String): Flux<String> {
        return Mono.defer { assistant.chat(message).toMono() }
            .flatMapMany { tokenStream ->
                Flux.create { emitter ->
                    tokenStream.onNext {
                        emitter.next(it)
                    }.onComplete {
                        emitter.complete()
                    }.onError {
                        emitter.complete()
                    }.start()
                }.timeout(Duration.ofSeconds(5))
            }
    }
}