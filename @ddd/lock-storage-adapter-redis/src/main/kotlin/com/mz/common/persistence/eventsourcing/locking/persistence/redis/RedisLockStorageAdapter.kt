package com.mz.common.persistence.eventsourcing.locking.persistence.redis

import com.mz.common.persistence.eventsourcing.locking.persistence.*
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

@Component
class RedisLockStorageAdapter(
    private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate
) : LockStorageAdapter {
    override fun acquireLock(request: AcquireLock, timeout: Duration): Mono<LockAcquired> {
        val key = request.getKey()
        val identifier = request.getIdentifier()

        return reactiveStringRedisTemplate.opsForValue()
            .setIfAbsent(key, identifier, timeout)
            .filter { it }
            .map { LockAcquired(key, Instant.now()) }
    }

    override fun releaseLock(releaseLock: ReleaseLock, timeout: Duration): Mono<LockReleased> {
        val key = releaseLock.getKey()

        return reactiveStringRedisTemplate.opsForValue()
            .delete(key)
            .filter { it }
            .map { LockReleased(key, Instant.now()) }
            .switchIfEmpty(Mono.error(IllegalStateException("Unable to release lock for the $key, because lock is not present")))
            .timeout(timeout)
    }

}