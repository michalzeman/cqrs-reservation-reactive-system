package com.mz.reservationsystem

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mz.common.components.CommonComponentsConfiguration
import com.mz.common.components.json.registerRequiredModules
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.redis.wiring.RedisLockStorageAdapterConfiguration
import com.mz.ddd.common.query.wiring.DomainViewConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    EventStorageAdapterCassandraConfiguration::class,
    RedisLockStorageAdapterConfiguration::class,
    DomainViewConfiguration::class,
    CommonComponentsConfiguration::class
)
@ComponentScan(value = ["com.mz.reservationsystem.domain.**"])
class ReservationSystemConfiguration {
    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerRequiredModules()
    }
}