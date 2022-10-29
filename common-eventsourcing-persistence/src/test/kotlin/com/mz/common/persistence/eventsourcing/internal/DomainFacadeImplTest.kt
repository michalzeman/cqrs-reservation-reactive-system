package com.mz.common.persistence.eventsourcing.internal

import com.mz.common.persistence.eventsourcing.DomainFacade
import com.mz.common.persistence.eventsourcing.aggregate.AggregateRepository
import com.mz.common.persistence.eventsourcing.aggregate.CommandEffect
import com.mz.common.persistence.eventsourcing.internal.util.*
import com.mz.reservation.common.api.domain.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.any
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import reactor.core.publisher.Mono

@ExtendWith(MockitoExtension::class)
internal class DomainFacadeImplTest {

    @Test
    fun execute() {
        val commandEffectMock = mock<CommandEffect<TestAggregate, TestEvent>> {
            on { events } doReturn listOf(mock<TestAggregateCreated>())
        }
        val aggregateRepository = mock<AggregateRepository<TestAggregate, TestCommand, TestEvent>> {
            on { execute(any(Id::class.java), any(CreateTestAggregate::class.java)) } doReturn Mono.just(
                commandEffectMock
            )
        }
        val subject = subject(aggregateRepository) { _ -> "String" }

//        subject.execute()
    }

    @Test
    fun executeAndReturnEvents() {
    }

    @Test
    fun findById() {
    }

    private fun subject(
        aggregateRepository: AggregateRepository<TestAggregate, TestCommand, TestEvent>,
        aggregateMapper: (TestAggregate) -> String
    ): DomainFacade<TestAggregate, TestCommand, TestEvent, String> {
        return DomainFacadeImpl(aggregateRepository, aggregateMapper = aggregateMapper)
    }
}