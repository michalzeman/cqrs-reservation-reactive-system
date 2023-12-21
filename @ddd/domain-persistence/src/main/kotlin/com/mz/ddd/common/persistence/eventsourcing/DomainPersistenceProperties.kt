package com.mz.ddd.common.persistence.eventsourcing

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "event.sourcing.domain-persistence")
data class DomainPersistenceProperties(
    val eventsPerSnapshot: Int,
    val lockManager: LockManagerProperties
)

data class LockManagerProperties(
    val numberOfAttempts: Int,
    val lockAcquireTimeoutOffSingleAttempt: Duration,
    val lockReleaseTimeout: Duration
)