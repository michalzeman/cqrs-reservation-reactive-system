package com.mz.reservationsystem.aiagent.domain.agent

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.domain.Assistant
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

enum class RedirectAction {
    USER_REGISTRATION,
    RESERVATION,
    RESERVATION_VIEW,
}

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
) : AgentRequest()

@Serializable
@SerialName("chat-response")
data class ChatResponse(
    override val chatId: Id,
    override val message: Content,
) : AgentResponse()

@Serializable
@SerialName("redirect-response")
data class RedirectResponse(
    override val chatId: Id,
    override val message: Content,
    val action: RedirectAction,
) : AgentResponse()

@Component
class AgentManager(
    private val assistant: Assistant,
    private val chatClassification: ChatClassification
) {

    companion object {
        private val logger = LogFactory.getLog(AgentManager::class.java)
    }

    fun execute(request: AgentRequest, finished: () -> Unit): Flux<AgentResponse> = when (request) {
        is NewChatRequest -> handleUnknownUserRequest(request)
        is ChatRequest -> handleChatRequest(request)
    }.doFinally { finished() }

    private fun handleUnknownUserRequest(request: NewChatRequest): Flux<AgentResponse> {
        val id = newId()
        return checkChat(id, request.message.value, this::chat)
    }

    private fun handleChatRequest(request: ChatRequest): Flux<AgentResponse> =
        checkChat(request.chatId, request.message.value, this::chat)

    private fun checkChat(id: Id, message: String, chat: (Id, String) -> Flux<AgentResponse>): Flux<AgentResponse> {
        val classification = chatClassification.relatedToReservationSystem(message)
        return if (!classification.result) Flux.fromIterable(classification.reason.split("\\s"))
            .map { ChatResponse(id, Content(it)) }
            .cast(AgentResponse::class.java)
            .delayElements(Duration.ofMillis(100))
        else chat(id, message)
    }

    private fun chat(id: Id, message: String): Flux<AgentResponse> {
        return Mono.defer { assistant.chat(id, message).toMono() }
            .flatMapMany { tokenStream ->
                Flux.create { emitter ->
                    tokenStream
                        .onNext {
                            emitter.next(it)
                        }
                        .onComplete { message ->
                            logger.trace("Chat completed, message: $message")
                            emitter.complete()
                        }
                        .onError {
                            logger.error(it)
                            emitter.complete()
                        }
                        .start()
                }.timeout(Duration.ofSeconds(5))
            }.map { ChatResponse(id, Content(it)) }
    }

}