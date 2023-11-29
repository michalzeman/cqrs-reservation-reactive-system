package com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.RedisTemplate


@Configuration
@ComponentScan("com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis")
class RedisLockStorageAdapterConfiguration(
    @Value("\${spring.redis.host}") val host: String,
    @Value("\${spring.redis.port}") val port: Int
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(host, port))
    }

    @Bean
    fun redisTemplate(lettuceConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.setConnectionFactory(lettuceConnectionFactory)
        return template
    }

    @Bean
    fun reactiveStringRedisTemplate(redisConnectionFactory: LettuceConnectionFactory): ReactiveStringRedisTemplate {
        return ReactiveStringRedisTemplate(redisConnectionFactory)
    }
}