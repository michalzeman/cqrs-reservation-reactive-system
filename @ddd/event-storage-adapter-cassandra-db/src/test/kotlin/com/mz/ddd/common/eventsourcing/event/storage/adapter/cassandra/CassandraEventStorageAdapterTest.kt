package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence.EventJournalEntity
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence.EventJournalRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence.SnapshotEntity
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence.SnapshotRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import com.mz.ddd.common.shared.test.cassandra.waitForDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import java.nio.ByteBuffer
import java.time.Instant


@SpringBootTest(classes = [EventStorageAdapterCassandraConfiguration::class])
@ActiveProfiles("test")
@Tag("systemChecks")
class CassandraEventStorageAdapterTest {

    @Autowired
    lateinit var cut: EventStorageAdapter

    @Autowired
    internal lateinit var eventJournalRepository: EventJournalRepository

    @Autowired
    internal lateinit var snapshotRepository: SnapshotRepository

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            waitForDatabase("ddd_testing_keyspace", "localhost", 9042, "datacenter1")
        }
    }

    @BeforeEach
    fun setUp() {
        eventJournalRepository.saveAll(
            listOf(
                EventJournalEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 1
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(1))
                    payloadType = "string"
                },
                EventJournalEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 2
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(2))
                    payloadType = "string"
                },
                EventJournalEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 3
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(3))
                    payloadType = "string"
                },
                EventJournalEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 4
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(1))
                    payloadType = "string"
                },
                EventJournalEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 5
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(2))
                    payloadType = "string"
                },
            )
        ).log()
            .then()
            .block()

        snapshotRepository.saveAll(
            listOf(
                SnapshotEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 1
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(1))
                    payloadType = "string"
                },
                SnapshotEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 3
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(2))
                    payloadType = "string"
                }
            )
        ).log()
            .then()
            .block()
    }

    @AfterEach
    fun tearDown() {
        eventJournalRepository.deleteAll().block()
        snapshotRepository.deleteAll().block()
    }

    @Test
    fun `readEvents by aggregate id`() {
        StepVerifier.create(cut.readEvents("1", 2).map { it.sequenceNumber })
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .expectNext(5)
            .verifyComplete()
    }

    @Test
    fun `readSnapshot by aggregate id`() {
        StepVerifier.create(cut.readSnapshot("1"))
            .assertNext {
                assertThat(it.eventJournals.size).isEqualTo(2)
                assertThat(it.eventJournals.map { ev -> ev.sequenceNumber }).isEqualTo(listOf(4L, 5L))
            }
            .verifyComplete()
    }

    @Test
    fun `getEventJournalSequenceNumber by SequenceNumberQuery`() {
        StepVerifier.create(cut.getEventJournalSequenceNumber(SequenceNumberQuery("1", "TestingAggregate")))
            .expectNext(5)
            .verifyComplete()
    }

    @Test
    fun `save snapshot`() {
        val snapshot = Snapshot(
            aggregateId = "1",
            sequenceNumber = 6,
            createdAt = Instant.now(),
            tag = "TestingAggregate",
            payload = byteArrayOf(1),
            payloadType = "string"
        )

        StepVerifier.create(cut.save(snapshot))
            .verifyComplete()

        StepVerifier.create(cut.readSnapshot("1"))
            .assertNext {
                assertThat(it.sequenceNumber).isEqualTo(6)
                assertThat(it.eventJournals.size).isEqualTo(0)
            }
            .verifyComplete()
    }
}