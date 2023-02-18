package com.mz.ddd.common.persistence.eventsourcing.locking

import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockAcquired
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import reactor.core.publisher.Mono

interface LockManager {

    fun acquireLock(request: AcquireLock): Mono<LockAcquired>

    fun releaseLock(releaseLock: ReleaseLock): Mono<LockReleased>
}