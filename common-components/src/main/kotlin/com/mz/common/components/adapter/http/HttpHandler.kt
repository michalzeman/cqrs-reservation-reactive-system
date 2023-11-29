package com.mz.common.components.adapter.http

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

data class ErrorMessage(val error: String)

interface HttpHandler {
    fun route(): RouterFunction<ServerResponse>
}

fun <T : Any> ServerResponse.BodyBuilder.mapToResponse(result: T): Mono<ServerResponse> {
    return contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(result))
}

fun <E : Throwable> onError(e: E, logger: (E) -> Unit): Mono<ServerResponse> {
    return Mono.fromCallable {
        logger(e)
        ErrorMessage(e.message ?: "")
    }.flatMap { error ->
        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error))
    }
}
