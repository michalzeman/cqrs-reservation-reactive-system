package com.mz.reservationsystem.aiagent.adapter.reservation.wiring

import com.mz.common.components.WebClientApiGatewayBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ReservationAdapterConfiguration(
    private val webClientApiGatewayBuilder: WebClientApiGatewayBuilder
) {

    @Bean
    fun reservationWebClient(): WebClient {
        return webClientApiGatewayBuilder.build("reservation-system")
    }
}