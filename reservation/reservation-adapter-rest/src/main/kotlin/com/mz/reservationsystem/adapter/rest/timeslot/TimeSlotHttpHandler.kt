package com.mz.reservationsystem.adapter.rest.timeslot

import com.mz.common.components.adapter.http.HttpHandler
import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.adapter.rest.timeslot.model.TimeSlotCommandRequest
import com.mz.reservationsystem.domain.api.timeslot.BookTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.domain.timeslot.TimeSlotApi
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
class TimeSlotHttpHandler(private val timeSlotApi: TimeSlotApi) : HttpHandler {
    override fun route(): RouterFunction<ServerResponse> {
        val route = RouterFunctions
            .route(
                RequestPredicates.POST("").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::createTimeSlot
            )
            .andRoute(
                RequestPredicates.PUT("").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::updateTimeSlot
            )
            .andRoute(
                RequestPredicates.PUT("/book").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::bookTimeSlot
            )
            .andRoute(
                RequestPredicates.GET("/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::getById
            )

        return RouterFunctions.route()
            .nest(RequestPredicates.path("/reservation-system/time-slots"), Supplier { route })
            .build()
    }

    fun createTimeSlot(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(TimeSlotCommandRequest::class.java)
            .map(TimeSlotCommandRequest::toCommand)
            .cast(CreateTimeSlot::class.java)
            .flatMap(timeSlotApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
    }

    fun updateTimeSlot(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(TimeSlotCommandRequest::class.java)
            .map(TimeSlotCommandRequest::toCommand)
            .filter { it !is CreateTimeSlot }
            .flatMap(timeSlotApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun bookTimeSlot(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(TimeSlotCommandRequest::class.java)
            .map(TimeSlotCommandRequest::toCommand)
            .filter { it is BookTimeSlot }
            .flatMap(timeSlotApi::execute)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun getById(request: ServerRequest): Mono<ServerResponse> {
        return Mono.fromCallable { request.pathVariable("id") }
            .map(::Id)
            .flatMap(timeSlotApi::findById)
            .flatMap { (ServerResponse.accepted().bodyValue(it)) }
            .switchIfEmpty(ServerResponse.noContent().build())
    }

}