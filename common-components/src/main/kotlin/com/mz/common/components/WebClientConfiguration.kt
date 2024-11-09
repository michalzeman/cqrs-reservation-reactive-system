package com.mz.common.components

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.json.registerRequiredModules
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {

    @Bean
    fun webClientBuilder(): WebClient.Builder = getWebClientBuilder()

    private fun getWebClientBuilder(): WebClient.Builder {
        val objectMapper = jacksonObjectMapper().registerRequiredModules()
        val strategies = ExchangeStrategies
            .builder()
            .codecs { configurer ->
                configurer.defaultCodecs()
                    .jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, APPLICATION_JSON))
                configurer.defaultCodecs()
                    .jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, APPLICATION_JSON))
            }.build()

        return WebClient.builder()
            .exchangeStrategies(strategies)
    }
}