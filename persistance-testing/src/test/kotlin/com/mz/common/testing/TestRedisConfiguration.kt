package com.mz.common.testing

import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@TestConfiguration
open class TestRedisConfiguration(redisProperties: RedisProperties) {

    val redisServer: RedisServer = RedisServer(redisProperties.redisPort)

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }

}