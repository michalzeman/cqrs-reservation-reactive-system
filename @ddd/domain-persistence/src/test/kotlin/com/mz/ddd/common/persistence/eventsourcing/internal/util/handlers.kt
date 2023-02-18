package com.mz.ddd.common.persistence.eventsourcing.internal.util

import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.command.AggregateCommandHandler
import com.mz.ddd.common.api.domain.event.AggregateEventHandler
import com.mz.ddd.common.api.util.Try

class TestCommandHandler : AggregateCommandHandler<TestAggregate, TestCommand, TestEvent> {
    override fun execute(aggregate: TestAggregate, command: TestCommand): Try<List<TestEvent>> {
        return when (aggregate) {
            is EmptyTestAggregate -> Try { executeOnEmptyAggregate(aggregate, command) }
            is ExistingTestAggregate -> Try { executeAggregate(aggregate, command) }
        }
    }

    private fun executeOnEmptyAggregate(aggregate: EmptyTestAggregate, cmd: TestCommand): List<TestEvent> {
        return when (cmd) {
            is CreateTestAggregate -> aggregate.verify(cmd)
            else -> throw RuntimeException("Wrong command type for the existing EmptyTestAggregate")
        }
    }

    private fun executeAggregate(aggregate: ExistingTestAggregate, cmd: TestCommand): List<TestEvent> {
        return when (cmd) {
            is UpdateTestValue -> aggregate.verify(cmd)
            else -> throw RuntimeException("Wrong command type for the existing ExistingTestAggregate")
        }
    }
}

class TestEventHandler : AggregateEventHandler<TestAggregate, TestEvent> {
    override fun apply(aggregate: TestAggregate, event: TestEvent): TestAggregate {
        return when (aggregate) {
            is EmptyTestAggregate -> applyForEmptyAggregate(aggregate, event)
            is ExistingTestAggregate -> applyOnExistingAggregate(aggregate, event)
        }
    }

    private fun applyForEmptyAggregate(aggregate: EmptyTestAggregate, event: DomainEvent): TestAggregate {
        return when (event) {
            is TestAggregateCreated -> aggregate.apply(event)
            else -> aggregate
        }
    }

    private fun applyOnExistingAggregate(aggregate: ExistingTestAggregate, event: DomainEvent): TestAggregate {
        return when (event) {
            is TestValueUpdated -> aggregate.apply(event)
            else -> aggregate
        }
    }
}