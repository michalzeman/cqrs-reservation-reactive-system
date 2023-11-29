package com.mz.customer.adapter.rest

import com.mz.common.components.adapter.http.onError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class CustomerAdapterRestConfiguration {

    @Autowired
    lateinit var customerHttpHandler: CustomerHttpHandler

    @Bean
    fun statisticRoute(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .add(customerHttpHandler.route())
            .onError(
                Throwable::class.java
            ) { throwable: Throwable, _ ->
                onError(throwable) { }
            }
            .build()
    }

}