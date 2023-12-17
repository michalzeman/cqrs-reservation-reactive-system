package com.mz.customer.adapter.rest

import com.mz.common.components.adapter.http.HttpHandler
import com.mz.customer.adapter.rest.model.CustomerCommandRequest
import com.mz.customer.domain.CustomerApi
import com.mz.customer.domain.api.RegisterCustomer
import com.mz.ddd.common.api.domain.Id
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.PUT
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.function.Supplier

@Component
class CustomerHttpHandler(private val customerApi: CustomerApi) : HttpHandler {
    override fun route(): RouterFunction<ServerResponse> {
        val route = RouterFunctions
            .route(POST("").and(accept(MediaType.APPLICATION_JSON)), this::registerCustomer)
            .andRoute(PUT("").and(accept(MediaType.APPLICATION_JSON)), this::updateCustomer)
            .andRoute(GET("/{id}").and(accept(MediaType.APPLICATION_JSON)), this::getById)

        return RouterFunctions.route()
            .nest(path("/customers"), Supplier { route })
            .build()
    }

    fun registerCustomer(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(CustomerCommandRequest::class.java)
            .map(CustomerCommandRequest::toCommand)
            .cast(RegisterCustomer::class.java)
            .flatMap(customerApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
    }

    fun updateCustomer(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(CustomerCommandRequest::class.java)
            .map(CustomerCommandRequest::toCommand)
            .filter { it !is RegisterCustomer }
            .flatMap(customerApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun getById(request: ServerRequest): Mono<ServerResponse> {
        return Mono.fromCallable { request.pathVariable("id") }
            .map(::Id)
            .flatMap(customerApi::findById)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }
}