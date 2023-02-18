package com.mz.ddd.common.persistence.eventsourcing.locking.internal

import com.mz.ddd.common.persistence.eventsourcing.locking.LockManager
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.*
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

internal class LockManagerImpl(
    private val lockStorageAdapter: LockStorageAdapter
) : LockManager {

    private val timeOutForSingleAttempt = Duration.ofMillis(100)

    private val lockedDurationThreshold = Duration.ofSeconds(5)

    private val numberOfAttempts = 20

    override fun acquireLock(request: AcquireLock): Mono<LockAcquired> = lockStorageAdapter
        .acquireLock(request, lockedDurationThreshold)
        .repeatWhenEmpty(numberOfAttempts) {
            Mono.delay(timeOutForSingleAttempt, Schedulers.boundedElastic())
                .then(acquireLock(request))
        }

    override fun releaseLock(releaseLock: ReleaseLock): Mono<LockReleased> = lockStorageAdapter
        .releaseLock(releaseLock, lockedDurationThreshold)

}