package com.mz.reservationsystem.adapter.rest.wiring

import com.mz.common.components.adapter.http.onError
import com.mz.reservationsystem.adapter.rest.reservation.ReservationHttpHandler
import com.mz.reservationsystem.adapter.rest.timeslot.TimeSlotHttpHandler
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class ReservationAdapterRestConfiguration {

    companion object {
        private val logger = LogFactory.getLog(ReservationAdapterRestConfiguration::class.java)
    }

    @Autowired
    private lateinit var timeSlotEventHandler: TimeSlotHttpHandler

    @Autowired
    private lateinit var reservationHttpHandler: ReservationHttpHandler

    @Bean
    fun reservationRoute(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .add(timeSlotEventHandler.route())
            .add(reservationHttpHandler.route())
            .onError(
                Throwable::class.java
            ) { throwable: Throwable, _ ->
                onError(throwable) { logger.error(it) }
            }
            .build()
    }
}