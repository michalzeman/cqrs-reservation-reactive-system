package com.mz.common.persistence.eventsourcing.locking.persistence.inmemory

import com.mz.common.persistence.eventsourcing.locking.AcquireLock
import com.mz.common.persistence.eventsourcing.locking.ReleaseLock
import com.mz.reservation.common.api.domain.uuid
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.Duration

class InMemoryLockStorageAdapterTest {

    private val lockStorageAdapter = InMemoryLockStorageAdapter(Duration.ofSeconds(2), Duration.ofSeconds(5))

    @Test
    fun `acquire lock when key is not locked, then locked is acquired`() {
        val key = uuid()
        StepVerifier.create(
            lockStorageAdapter.acquireLock(
                AcquireLock({ key }, { "Identifier $key" }),
                Duration.ofSeconds(1)
            )
        )
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `try to acquire lock when key is already locked, then locked isn't acquired`() {
        val timeOut = Duration.ofSeconds(1)
        val key = uuid()

        StepVerifier.create(
            lockStorageAdapter.acquireLock(AcquireLock({ key }, { "Identifier $key" }), timeOut)
                .then(lockStorageAdapter.acquireLock(AcquireLock({ key }, { "Identifier 2" }), timeOut))
        )
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `when lock is set up and release lock is called then lock is released`() {
        val timeOut = Duration.ofSeconds(1)
        val key = uuid()
        StepVerifier.create(
            lockStorageAdapter.acquireLock(AcquireLock({ key }, { "Identifier $key" }), timeOut)
                .then(lockStorageAdapter.releaseLock(ReleaseLock { key }, timeOut))
        )
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `when lock isn't set up and release lock is called then error is triggered`() {
        val timeOut = Duration.ofSeconds(1)
        val key = uuid()
        StepVerifier.create(lockStorageAdapter.releaseLock(ReleaseLock { key }, timeOut))
            .verifyError(IllegalStateException::class.java)
    }

    @Test
    fun `when lock is set up and release lock is called after the automatic clean up then lock isn't released`() {
        val timeOut = Duration.ofSeconds(1)
        val key = uuid()
        StepVerifier.create(
            lockStorageAdapter.acquireLock(AcquireLock({ key }, { "Identifier $key" }), timeOut)
                .delayElement(Duration.ofSeconds(6))
                .then(lockStorageAdapter.releaseLock(ReleaseLock { key }, timeOut))
        )
            .verifyError(IllegalStateException::class.java)
    }
}