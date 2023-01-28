package com.mz.common.persistence.eventsourcing.locking.persistence

import com.mz.common.persistence.eventsourcing.locking.AcquireLock
import com.mz.common.persistence.eventsourcing.locking.LockAcquired
import com.mz.common.persistence.eventsourcing.locking.LockReleased
import com.mz.common.persistence.eventsourcing.locking.ReleaseLock
import reactor.core.publisher.Mono
import java.time.Duration

interface LockStorageAdapter {

    fun acquireLock(request: AcquireLock, timeout: Duration): Mono<LockAcquired>

    fun releaseLock(releaseLock: ReleaseLock, timeout: Duration): Mono<LockReleased>

}