package com.mz.ddd.common.testing

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class RedisProperties(
    @Value("\${spring.redis.port}") var redisPort: Int,
    @Value("\${spring.redis.host}") var redisHost: String
)