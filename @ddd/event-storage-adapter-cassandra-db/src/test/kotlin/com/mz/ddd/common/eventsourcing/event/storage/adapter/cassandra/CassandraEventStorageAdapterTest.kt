package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.EventJournalEntity
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.EventJournalRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.SnapshotEntity
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance.SnapshotRepository
import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import java.nio.ByteBuffer
import java.time.Instant

@SpringBootTest(classes = [EventStorageAdapterCassandraConfiguration::class])
@ActiveProfiles("test")
class CassandraEventStorageAdapterTest {

    @Autowired
    lateinit var cut: EventStorageAdapter

    @Autowired
    internal lateinit var eventJournalRepository: EventJournalRepository

    @Autowired
    internal lateinit var snapshotRepository: SnapshotRepository

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
                assertThat(it.eventJournals.size).isEqualTo(3)
                assertThat(it.eventJournals.map { ev -> ev.sequenceNumber }).isEqualTo(listOf(3L, 4L, 5L))
            }
            .verifyComplete()
    }

    @Test
    fun `getEventJournalSequenceNumber by SequenceNumberQuery`() {
        StepVerifier.create(cut.getEventJournalSequenceNumber(SequenceNumberQuery("1", "TestingAggregate")))
            .expectNext(5)
            .verifyComplete()
    }
}