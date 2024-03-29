package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistence

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.EventStorageAdapterCassandraConfiguration
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
class SnapshotRepositoryTest {

    @Autowired
    internal lateinit var repository: SnapshotRepository

    // beforeEach method that generate and store testing data
    @BeforeEach
    fun setUp() {
        // create 3 instances of EvenJournalEntity with respecting sequenceNr and store it into the DB
        repository.saveAll(
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
                    sequenceNr = 2
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(2))
                    payloadType = "string"
                },
                SnapshotEntity().apply {
                    aggregateId = "1"
                    sequenceNr = 3
                    createdAt = Instant.now()
                    tag = "TestingAggregate"
                    payload = ByteBuffer.wrap(byteArrayOf(3))
                    payloadType = "string"
                }
            )
        ).log()
            .then()
            .block()
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAll().block()
    }

    @Test
    fun `find by aggregate id`() {
        StepVerifier.create(repository.findByAggregateId("1"))
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    fun `find by aggregateId and started with sequenceNr`() {
        StepVerifier.create(repository.findByAggregateIdAndSequenceNrGreaterThanEqual("1", 2))
            .expectNextCount(2)
            .verifyComplete()
    }


}