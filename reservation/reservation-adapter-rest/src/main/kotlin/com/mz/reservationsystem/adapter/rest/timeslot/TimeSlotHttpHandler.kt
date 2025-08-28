package com.mz.reservationsystem.adapter.rest.timeslot

import com.mz.common.components.adapter.http.HttpHandler
import com.mz.ddd.common.api.domain.Id
import com.mz.reservationsystem.adapter.model.timeslot.TimeSlotCommandRequest
import com.mz.reservationsystem.domain.api.timeslot.BookTimeSlot
import com.mz.reservationsystem.domain.api.timeslot.CreateTimeSlot
import com.mz.reservationsystem.application.timeslot.*
import kotlinx.datetime.Instant
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.util.function.Supplier

@Component
class TimeSlotHttpHandler(
    private val timeSlotApi: TimeSlotApi,
    private val findTimeSlotByTimesUseCase: FindTimeSlotByTimesUseCase
) : HttpHandler {
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
            .andRoute(
                RequestPredicates.GET("").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                this::search
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
            .switchIfEmpty(ServerResponse.badRequest().build())
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

    fun search(request: ServerRequest): Mono<ServerResponse> {
        return Mono.fromCallable { request.mapToQuery() }
            .mapNotNull { it }
            .flatMap { findTimeSlotByTimesUseCase(it!!).collectList() }
            .flatMap { ServerResponse.ok().bodyValue(it) }
            .switchIfEmpty(ServerResponse.noContent().build())
    }
}

internal fun String.isBoolean(): Boolean = when (this.lowercase()) {
    "true" -> true
    "false" -> true
    else -> false
}

internal fun ServerRequest.mapToQuery(): TimeSlotQuery? {
    val startTime = queryParam("start_time").orElse("")
    val endTime = queryParam("end_time").orElse("")
    val booked = queryParam("booked").orElse("")

    return when {
        startTime.isNotEmpty() && endTime.isNotEmpty() && booked.isBoolean() -> {
            val queryTimeSlotBetweenTimes = FindTimeSlotBetweenTimes(
                startTime = Instant.parse(startTime),
                endTime = Instant.parse(endTime)
            )
            val bookedQuery = FindTimeSlotByBooked(booked.toBoolean())

            FindTimeSlotsByConditions(
                setOf(
                    queryTimeSlotBetweenTimes,
                    bookedQuery
                )
            )
        }

        startTime.isNotEmpty() && endTime.isNotEmpty() -> {
            FindTimeSlotBetweenTimes(
                startTime = Instant.parse(startTime),
                endTime = Instant.parse(endTime)
            )
        }

        else -> null
    }
}

