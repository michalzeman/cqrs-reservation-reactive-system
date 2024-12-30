package com.mz.common.components

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.client.WebClient


@AutoConfiguration
@Import(
    WebClientConfiguration::class
)
@ConditionalOnProperty(name = ["adapter.api-gateway.base-url"])
class WebClientApiGatewayAdapterConfiguration(
    @Value("\${adapter.api-gateway.base-url}") private val apiGatewayBasedUrl: String
) {
    @Autowired
    lateinit var webClientBuilder: WebClient.Builder

    @Bean
    fun webClientApiGatewayBuilder(): WebClientApiGatewayBuilder =
        WebClientApiGatewayBuilder(apiGatewayBasedUrl, webClientBuilder)
}

class WebClientApiGatewayBuilder(
    private val apiGatewayBasedUrl: String,
    private val webClientBuilder: WebClient.Builder
) {

    fun build(urlPredicate: String): WebClient = webClientBuilder.baseUrl("$apiGatewayBasedUrl/$urlPredicate")
        .build()
}