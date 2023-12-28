package com.mz.reservationsystem.timeslot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.json.registerRequiredModules
import com.mz.reservationsystem.ReservationAggregateConfiguration
import com.mz.reservationsystem.ReservationSystemConfiguration
import com.mz.reservationsystem.TimeSlotAggregateConfiguration
import com.mz.reservationsystem.adapter.rest.wiring.ReservationAdapterRestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@Import(
    TimeSlotAggregateConfiguration::class,
    ReservationAggregateConfiguration::class,
    ReservationAdapterRestConfiguration::class,
    ReservationSystemConfiguration::class
)
@ComponentScan("com.mz.reservationsystem.**")
@ActiveProfiles("test")
class TestReservationSystemConfiguration {
    @Bean
    fun webClient(): WebClient {
        val objectMapper = jacksonObjectMapper().registerRequiredModules()
        val strategies = ExchangeStrategies
            .builder()
            .codecs { configurer ->
                configurer.defaultCodecs()
                    .jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
                configurer.defaultCodecs()
                    .jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON))
            }.build()

        return WebClient.builder().exchangeStrategies(strategies).build()
    }
}