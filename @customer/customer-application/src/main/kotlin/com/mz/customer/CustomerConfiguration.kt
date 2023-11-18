package com.mz.customer

import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class CustomerConfiguration {

    @Bean
    fun session(): CqlSession {
        return CqlSession.builder().withKeyspace("customer_keyspace").build()
    }

}