package com.mz.reservationsystem.adapter.rest.reservation

import com.mz.common.components.adapter.http.HttpHandler
import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.adapter.rest.reservation.model.ReservationCommandRequest
import com.mz.reservationsystem.domain.reservation.ReservationApi
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.function.Supplier

@Component
class ReservationHttpHandler(private val reservationApi: ReservationApi) : HttpHandler {
    override fun route(): RouterFunction<ServerResponse> {
        val route = RouterFunctions
            .route(
                RequestPredicates.POST("").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::requestReservation
            )
            .andRoute(
                RequestPredicates.PUT("/decline").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::declineReservation
            )
            .andRoute(
                RequestPredicates.PUT("/accept").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::acceptReservation
            )
            .andRoute(
                RequestPredicates.GET("/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::getById
            )

        return RouterFunctions.route()
            .nest(RequestPredicates.path("/reservation-system/reservations"), Supplier { route })
            .build()
    }

    fun requestReservation(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(ReservationCommandRequest::class.java)
            .map(ReservationCommandRequest::toCommand)
            .flatMap(reservationApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
    }

    fun declineReservation(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(ReservationCommandRequest::class.java)
            .map(ReservationCommandRequest::toCommand)
            .flatMap(reservationApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun acceptReservation(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(ReservationCommandRequest::class.java)
            .map(ReservationCommandRequest::toCommand)
            .flatMap(reservationApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun getById(request: ServerRequest): Mono<ServerResponse> {
        return Mono.fromCallable { request.pathVariable("id") }
            .map(::Id)
            .flatMap(reservationApi::findById)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.noContent().build())
    }

}