package com.mz.ddd.common.persistence.eventsourcing

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@EnableConfigurationProperties(DomainPersistenceProperties::class)
@PropertySource("classpath:event-sourcing-domain-persistence.properties")
class DomainPersistenceConfiguration {
}