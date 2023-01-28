package com.mz.common.persistence.eventsourcing.locking

import reactor.core.publisher.Mono
import java.time.Instant

data class AcquireLock(val getKey: () -> String, val getIdentifier: () -> String)

data class LockAcquired(val key: String, val acquiredAt: Instant)

data class ReleaseLock(val getKey: () -> String)

data class LockReleased(val id: String, val releasedAt: Instant)

interface LockManager {

    fun acquireLock(request: AcquireLock): Mono<LockAcquired>

    fun releaseLock(releaseLock: ReleaseLock): Mono<LockReleased>
}