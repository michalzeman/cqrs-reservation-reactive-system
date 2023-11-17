package com.mz.ddd.common.persistence.eventsourcing

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "event.sourcing.domain-persistence")
data class DomainPersistenceProperties(
    val eventsPerSnapshot: Int
)
