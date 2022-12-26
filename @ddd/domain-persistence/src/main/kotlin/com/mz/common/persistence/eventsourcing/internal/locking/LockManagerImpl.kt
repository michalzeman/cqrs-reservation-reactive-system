package com.mz.common.persistence.eventsourcing.internal.locking

import com.mz.common.persistence.eventsourcing.locking.*
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant

@Component
internal class LockManagerImpl(
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate,
) : LockManager {

    private val timeOutForSingleAttempt = Duration.ofMillis(100)

    private val lockedDurationThreshold = Duration.ofSeconds(5)

    private val numberOfAttempts = 20

    override fun acquireLock(request: AcquireLock): Mono<LockAcquired> {
        val key = request.getKey()
        val value = request.getIdentifier()

        return reactiveStringRedisTemplate.opsForValue()
            .setIfAbsent(key, value, lockedDurationThreshold)
            .filter { it }
            .map { LockAcquired(key, Instant.now()) }
            .repeatWhenEmpty(numberOfAttempts) {
                Mono.delay(timeOutForSingleAttempt, Schedulers.boundedElastic())
                    .then(acquireLock(request))
            }
    }

    override fun releaseLock(releaseLock: ReleaseLock): Mono<LockReleased> {
        val key = releaseLock.getKey()

        return reactiveStringRedisTemplate.delete(key)
            .map { LockReleased(key, Instant.now()) }
    }

}