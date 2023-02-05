package com.mz.common.persistence.eventsourcing.locking.persistence

import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

interface LockStorageAdapter {

    fun acquireLock(request: AcquireLock, timeout: Duration): Mono<LockAcquired>

    fun releaseLock(releaseLock: ReleaseLock, timeout: Duration): Mono<LockReleased>

}

data class AcquireLock(val getKey: () -> String, val getIdentifier: () -> String)
data class LockAcquired(val key: String, val acquiredAt: Instant)
data class ReleaseLock(val getKey: () -> String)
data class LockReleased(val id: String, val releasedAt: Instant)