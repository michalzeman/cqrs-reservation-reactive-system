package com.mz.ddd.common.persistence.eventsourcing.aggregate

import com.mz.ddd.common.api.domain.newId
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.Snapshot
import com.mz.ddd.common.persistence.eventsourcing.event.EventRepository
import com.mz.ddd.common.persistence.eventsourcing.event.data.serd.adapter.EventSerDesAdapter
import com.mz.ddd.common.persistence.eventsourcing.internal.util.CreateTestAggregate
import com.mz.ddd.common.persistence.eventsourcing.internal.util.ExistingTestAggregate
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestAggregate
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestAggregateCreated
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestCommand
import com.mz.ddd.common.persistence.eventsourcing.internal.util.TestEvent
import com.mz.ddd.common.persistence.eventsourcing.locking.LockManager
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.AcquireLock
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockAcquired
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.LockReleased
import com.mz.ddd.common.persistence.eventsourcing.locking.persistence.ReleaseLock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class AggregateRepositoryImplTest {

    @Mock
    lateinit var aggregateProcessor: AggregateProcessor<TestAggregate, TestCommand, TestEvent>

    @Mock
    lateinit var lockManager: LockManager

    private lateinit var aggregate: ExistingTestAggregate

    @Mock
    lateinit var eventRepository: EventRepository<TestEvent, TestAggregate>

    @Mock
    lateinit var serDesAdapter: EventSerDesAdapter<TestEvent, TestAggregate>

    private lateinit var subject: AggregateRepository<TestAggregate, TestCommand, TestEvent>

    @BeforeEach
    fun beforeEach() {
        aggregate = mock<ExistingTestAggregate>()
        subject = AggregateRepositoryImpl(
            aggregateFactory = { aggregate },
            aggregateProcessor = aggregateProcessor,
            eventRepository = eventRepository,
            lockManager = lockManager,
            serDesAdapter = serDesAdapter
        )
    }

    @Test
    fun `when execution finish, the lockManager is called`() {
        val id = newId()
        val command: TestCommand = mock<CreateTestAggregate>()
        val lockAcquired = mock<LockAcquired>()
        val event: TestEvent = mock<TestAggregateCreated>()
        val lockReleased = mock<LockReleased>()
        val snapshot = mock<Snapshot>()
        val commandEffect = CommandEffect(
            aggregate = aggregate as TestAggregate,
            events = listOf(event)
        )

        whenever(serDesAdapter.deserialize(snapshot)).doReturn(aggregate)
        whenever(snapshot.eventJournals).doReturn(emptyList())

        whenever(lockManager.acquireLock(any<AcquireLock>())).thenReturn(lockAcquired.toMono())
        whenever(lockManager.releaseLock(any<ReleaseLock>())).thenReturn(lockReleased.toMono())

        whenever(aggregateProcessor.execute(aggregate as TestAggregate, command)).thenReturn(
            Result.success(commandEffect)
        )
        whenever(aggregateProcessor.applyEvents(any<TestAggregate>(), any<List<TestEvent>>())).thenReturn(aggregate)

        whenever(eventRepository.persistAll(commandEffect)).thenReturn(Mono.empty())
        whenever(eventRepository.readSnapshot(id)).thenReturn(snapshot.toMono())

        StepVerifier.create(subject.execute(id, command))
            .expectNextCount(1)
            .verifyComplete()

        verify(lockManager).releaseLock(any<ReleaseLock>())
    }

    @Test
    fun `when execution with exclusivePreExecute finish, the lockManager is called`() {
        val id = newId()
        val command: TestCommand = mock<CreateTestAggregate>()
        val lockAcquired = mock<LockAcquired>()
        val event: TestEvent = mock<TestAggregateCreated>()
        val lockReleased = mock<LockReleased>()
        val snapshot = mock<Snapshot>()
        val commandEffect = CommandEffect(
            aggregate = aggregate as TestAggregate,
            events = listOf(event)
        )

        whenever(serDesAdapter.deserialize(snapshot)).doReturn(aggregate)
        whenever(snapshot.eventJournals).doReturn(emptyList())

        whenever(lockManager.acquireLock(any<AcquireLock>())).thenReturn(lockAcquired.toMono())
        whenever(lockManager.releaseLock(any<ReleaseLock>())).thenReturn(lockReleased.toMono())

        whenever(aggregateProcessor.execute(aggregate as TestAggregate, command)).thenReturn(
            Result.success(commandEffect)
        )
        whenever(aggregateProcessor.applyEvents(any<TestAggregate>(), any<List<TestEvent>>())).thenReturn(aggregate)

        whenever(eventRepository.persistAll(commandEffect)).thenReturn(Mono.empty())
        whenever(eventRepository.readSnapshot(id)).thenReturn(snapshot.toMono())

        StepVerifier.create(subject.execute(id, command, { true.toMono() }))
            .expectNextCount(1)
            .verifyComplete()

        verify(lockManager).releaseLock(any<ReleaseLock>())
    }

    @Test
    fun `when exclusivePreExecute return false, then command is not executed and releaseLock is called`() {
        val id = newId()
        val command: TestCommand = mock<CreateTestAggregate>()
        val lockAcquired = mock<LockAcquired>()
        val lockReleased = mock<LockReleased>()

        whenever(lockManager.acquireLock(any<AcquireLock>())).thenReturn(lockAcquired.toMono())
        whenever(lockManager.releaseLock(any<ReleaseLock>())).thenReturn(lockReleased.toMono())

        StepVerifier.create(subject.execute(id, command, { false.toMono() }))
            .expectError(IllegalStateException::class.java)
            .verify()

        verify(lockManager).releaseLock(any<ReleaseLock>())
        verify(aggregateProcessor, never()).execute(any<TestAggregate>(), any<TestCommand>())
    }

    @Test
    fun `when exclusivePreExecute fails, then command is not executed and releaseLock is called`() {
        val id = newId()
        val command: TestCommand = mock<CreateTestAggregate>()
        val lockAcquired = mock<LockAcquired>()
        val lockReleased = mock<LockReleased>()

        whenever(lockManager.acquireLock(any<AcquireLock>())).thenReturn(lockAcquired.toMono())
        whenever(lockManager.releaseLock(any<ReleaseLock>())).thenReturn(lockReleased.toMono())

        StepVerifier.create(subject.execute(id, command, { Mono.error(IllegalStateException()) }))
            .expectError(IllegalStateException::class.java)
            .verify()

        verify(lockManager).releaseLock(any<ReleaseLock>())
        verify(aggregateProcessor, never()).execute(any<TestAggregate>(), any<TestCommand>())
    }

    @Test
    fun `when execution on processor failed, the lockManager is called`() {
        val id = newId()
        val command: TestCommand = mock<CreateTestAggregate>()
        val lockAcquired = mock<LockAcquired>()
        val lockReleased = mock<LockReleased>()
        val snapshot = mock<Snapshot>()

        whenever(serDesAdapter.deserialize(snapshot)).doReturn(aggregate)
        whenever(snapshot.eventJournals).doReturn(emptyList())

        whenever(lockManager.acquireLock(any<AcquireLock>())).thenReturn(lockAcquired.toMono())
        whenever(lockManager.releaseLock(any<ReleaseLock>())).thenReturn(lockReleased.toMono())

        whenever(aggregateProcessor.execute(aggregate as TestAggregate, command)).thenReturn(
            Result.failure(RuntimeException("Test exception"))
        )
        whenever(aggregateProcessor.applyEvents(any<TestAggregate>(), any<List<TestEvent>>())).thenReturn(aggregate)

        whenever(eventRepository.readSnapshot(id)).thenReturn(snapshot.toMono())

        StepVerifier.create(subject.execute(id, command))
            .expectError(RuntimeException::class.java)
            .verify()

        verify(lockManager).releaseLock(any<ReleaseLock>())
    }

}