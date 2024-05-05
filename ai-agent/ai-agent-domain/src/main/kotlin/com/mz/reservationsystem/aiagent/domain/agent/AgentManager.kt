package com.mz.reservationsystem.aiagent.domain.agent

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.domain.Assistant
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Serializable
sealed class AgentRequest {
    abstract val message: Content
}

@Serializable
sealed class AgentResponse {
    abstract val chatId: Id
    abstract val message: Content
}

@Serializable
@SerialName("new-chat-request")
data class NewChatRequest(override val message: Content) : AgentRequest()

@Serializable
@SerialName("chat-request")
data class ChatRequest(
    val chatId: Id,
    override val message: Content
): AgentRequest()

@Serializable
@SerialName("chat-response")
data class ChatResponse(
    override val chatId: Id,
    override val message: Content
) : AgentResponse()

@Component
class AgentManager(
    private val assistant: Assistant
) {

    fun execute(request: AgentRequest, finished: () -> Unit): Flux<AgentResponse> = when(request) {
        is NewChatRequest -> handleUnknownUserRequest(request)
        is ChatRequest -> handleChatRequest(request)
    }.doFinally { finished() }

    private fun handleUnknownUserRequest(request: NewChatRequest): Flux<AgentResponse> {
        val id = newId()
        return chat(id, request.message.value)
            .map { ChatResponse(id, Content(it)) }
    }

    private fun handleChatRequest(request: ChatRequest): Flux<AgentResponse> {
        return chat(request.chatId, request.message.value)
            .map { ChatResponse(request.chatId, Content(it)) }
    }

    private fun chat(id: Id, message: String): Flux<String> {
        return Mono.defer { assistant.chat(id, message).toMono() }
            .flatMapMany { tokenStream ->
                Flux.create { emitter ->
                    tokenStream.onNext { emitter.next(it) }
                        .onComplete {
                            emitter.complete()
                        }
                        .onError {
                            emitter.complete()
                        }
                        .start()
                }.timeout(Duration.ofSeconds(5))
            }
    }

}