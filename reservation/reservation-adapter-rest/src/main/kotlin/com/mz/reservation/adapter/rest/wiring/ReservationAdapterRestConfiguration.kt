package com.mz.reservation.adapter.rest.wiring

import com.mz.common.components.adapter.http.onError
import com.mz.reservation.adapter.rest.timeslot.TimeSlotHttpHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class ReservationAdapterRestConfiguration {

    @Autowired
    private lateinit var timeSlotEventHandler: TimeSlotHttpHandler

    @Bean
    fun reservationRoute(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .add(timeSlotEventHandler.route())
            .onError(
                Throwable::class.java
            ) { throwable: Throwable, _ ->
                onError(throwable) { }
            }
            .build()
    }
}