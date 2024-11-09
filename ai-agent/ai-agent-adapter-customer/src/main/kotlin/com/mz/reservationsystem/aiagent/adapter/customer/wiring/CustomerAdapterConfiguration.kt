package com.mz.reservationsystem.aiagent.adapter.customer.wiring

import com.mz.common.components.WebClientConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder

@Configuration
@Import(
    WebClientConfiguration::class
)
class CustomerAdapterConfiguration(
   @Value("\${adapter.api-gateway.base-url}") private val apiGatewayBasedUrl: String
) {

    @Bean
    fun customerWebClient(webClientBuilder: Builder): WebClient {
        return webClientBuilder.baseUrl("${apiGatewayBasedUrl}/customers").build()
    }

}