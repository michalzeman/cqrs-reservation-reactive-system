package com.mz.ddd.common.persistence.eventsourcing.locking.persistence.inmemory

import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockAcquired
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockStorageAdapter
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

/**
 * Data class using for internally for the cache
 * @param key -> key under which is stored into the cache
 * @param identifier -> identifier of who puts data into the cache
 * @param createdAt -> when data were putted into the cache, needed for a periodic cleanup
 */
internal data class LockData(val key: String, val identifier: String, val createdAt: Instant = Instant.now())

internal sealed interface LockResult

internal object LockFailed : LockResult

internal typealias DataCache = MutableMap<String, LockData>

internal data class LockSuccess(val key: String, val acquiredAt: Instant) : LockResult

/**
 * In memory Lock storage adapter. In case you need distributed locking, then please a different implementation of the
 * com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockStorageAdapter
 */
@Component
class InMemoryLockStorageAdapter(
    private val cacheCleanupInterval: Duration,
    private val cleanUpRetentionTime: Duration
) : LockStorageAdapter {

    private val cacheStorage = AtomicReference<DataCache>(mutableMapOf())

    init {
        scheduleCleanUp()
    }

    override fun acquireLock(request: AcquireLock, timeout: Duration): Mono<LockAcquired> {
        return Mono.create<LockResult> { sink ->
            cacheStorage.updateAndGet { cache ->
                val key = request.getKey()
                if (cache.containsKey(key)) {
                    sink.success(LockFailed)
                    cache
                } else {
                    val now = Instant.now()
                    cache[key] = LockData(key, request.getIdentifier(), now)
                    sink.success(LockSuccess(key, now))
                    cache
                }
            }
        }
            .timeout(timeout)
            .filter { it is LockSuccess }
            .cast(LockSuccess::class.java)
            .map { LockAcquired(it.key, it.acquiredAt) }
    }

    override fun releaseLock(releaseLock: ReleaseLock, timeout: Duration): Mono<LockReleased> {
        return Mono.create<LockReleased?> { sink ->
            val key = releaseLock.getKey()
            cacheStorage.updateAndGet { cache ->
                cache.remove(key)?.also {
                    sink.success(LockReleased(key, Instant.now()))
                } ?: run {
                    sink.error(IllegalStateException("Unable to release lock for the $key, because lock is not present"))
                }
                cache
            }
        }.timeout(timeout)
    }

    private fun scheduleCleanUp() {
        Mono.delay(cacheCleanupInterval, Schedulers.boundedElastic())
            .repeat()
            .subscribe { cacheStorage.updateAndGet { cleanUpOldLocks(it) } }
    }

    private fun cleanUpOldLocks(cache: DataCache): DataCache {
        val now = Instant.now()
        val retentionTime = now.minus(cleanUpRetentionTime)
        cache.values.removeIf { item -> retentionTime.isBefore(item.createdAt) }
        return cache
    }
}