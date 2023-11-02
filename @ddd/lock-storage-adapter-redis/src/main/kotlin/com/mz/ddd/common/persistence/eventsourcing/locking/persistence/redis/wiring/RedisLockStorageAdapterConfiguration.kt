package com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate

@Configuration
@ComponentScan("com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis")
class RedisLockStorageAdapterConfiguration(
    @Value("\${spring.redis.host}") val host: String,
    @Value("\${spring.redis.port}") val port: Int
) {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun redisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveStringRedisTemplate {
        return ReactiveStringRedisTemplate(factory)
    }
}