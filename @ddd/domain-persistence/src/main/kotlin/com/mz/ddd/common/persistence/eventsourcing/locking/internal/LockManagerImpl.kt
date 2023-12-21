package com.mz.ddd.common.persistence.eventsourcing.locking.internal

import com.mz.ddd.common.persistence.eventsourcing.DomainPersistenceProperties
import com.mz.ddd.common.persistence.eventsourcing.locking.LockManager
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockAcquired
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockStorageAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 * LockManagerImpl is responsible for acquiring and releasing locks.
 * It uses [LockStorageAdapter] to store locks.
 */
internal class LockManagerImpl(
    private val lockStorageAdapter: LockStorageAdapter,
    private val properties: DomainPersistenceProperties
) : LockManager {

    private val lockManagerProperties = properties.lockManager

    override fun acquireLock(request: AcquireLock): Mono<LockAcquired> = lockStorageAdapter
        .acquireLock(request, lockManagerProperties.lockReleaseTimeout)
        .repeatWhenEmpty(lockManagerProperties.numberOfAttempts) {
            Mono.delay(lockManagerProperties.lockAcquireTimeoutOffSingleAttempt, Schedulers.boundedElastic())
                .then(acquireLock(request))
        }

    override fun releaseLock(releaseLock: ReleaseLock): Mono<LockReleased> = lockStorageAdapter
        .releaseLock(releaseLock, lockManagerProperties.lockReleaseTimeout)

}