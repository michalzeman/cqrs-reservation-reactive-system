package com.mz.ddd.common.persistence.eventsourcing.internal.util

fun EmptyTestAggregate.verify(cmd: CreateTestAggregate): List<TestEvent> {
    return when (cmd.value) {
        is StringValueParam -> listOf(
            TestAggregateCreated(
                aggregateId = this.aggregateId,
                value = ValueVo(cmd.value.value),
                correlationId = cmd.correlationId
            )
        )

        else -> throw RuntimeException("For creation only string values are allowed")
    }
}

fun EmptyTestAggregate.apply(event: TestAggregateCreated): ExistingTestAggregate {
    return ExistingTestAggregate(aggregateId = this.aggregateId, event.value)
}

fun ExistingTestAggregate.verify(cmd: UpdateTestValue): List<TestEvent> {
    assert(this.aggregateId == cmd.aggregateId)
    return listOf(TestValueUpdated(aggregateId, value = value.copy(value = "${this.value.value} ${cmd.value.value}")))
}

fun ExistingTestAggregate.apply(event: TestValueUpdated): ExistingTestAggregate {
    return this.copy(value = event.value)
}