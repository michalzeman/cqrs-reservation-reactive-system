package com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.persistance

import com.mz.ddd.common.eventsourcing.event.storage.adapter.cassandra.wiring.TestEventStorageAdapterCassandraConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import java.nio.ByteBuffer
import java.time.Instant

@SpringBootTest(classes = [TestEventStorageAdapterCassandraConfiguration::class])
@ActiveProfiles("test")
class EventJournalRepositoryTest {

    @Autowired
    internal lateinit var eventJournalRepository: EventJournalRepository

    // beforeEach method that generate and store testing data
    @BeforeEach
    fun setUp() {
        // create 3 instances of EvenJournalEntity with respecting sequenceNr and store it into the DB
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
                }
            )
        ).log()
            .then()
            .block()
    }

    @AfterEach
    fun tearDown() {
        eventJournalRepository.deleteAll().block()
    }

    @Test
    fun `find by aggregate id`() {
        StepVerifier.create(eventJournalRepository.findByAggregateId("1").mapNotNull { it.sequenceNr })
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    @Test
    fun `find by aggregateId and started with sequenceNr`() {
        StepVerifier.create(eventJournalRepository.findByAggregateIdAndSequenceNrGreaterThanEqual("1", 2)
            .mapNotNull { it.sequenceNr }
        )
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    @Test
    fun `find max sequenceNr by aggregateId`() {
        StepVerifier.create(eventJournalRepository.findMaxSequenceNuByAggregateId("1"))
            .expectNext(3)
            .verifyComplete()
    }


}