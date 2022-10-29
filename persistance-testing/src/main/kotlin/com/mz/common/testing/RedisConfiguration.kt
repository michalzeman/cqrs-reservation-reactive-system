package com.mz.common.testing

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories


@Configuration
@EnableRedisRepositories
open class RedisConfiguration {

    @Bean
    open fun reactiveRedisConnectionFactory(redisProperties: RedisProperties): ReactiveRedisConnectionFactory? {
        return LettuceConnectionFactory(
            redisProperties.redisHost,
            redisProperties.redisPort
        )
    }

}