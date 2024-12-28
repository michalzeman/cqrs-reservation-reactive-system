package com.mz.ddd.common.view

import kotlinx.datetime.Instant

enum class OperationType {
    AND, OR, LIKE, NOT, EQUALS, GREATER_THAN, LESS_THAN
}

sealed interface QueryOperation

data class DomainViewQuery(
    val queryableDataSet: Set<QueryData<*>>,
    val operationType: OperationType = OperationType.OR
) : QueryOperation

sealed class QueryData<T> {
    abstract val domainTag: String
}

data class BetweenInstantQuery(
    override val domainTag: String,
    val startTimePropertyName: String,
    val endTimePropertyName: String,
    val startTime: Instant,
    val endTime: Instant
) : QueryData<Instant>()

sealed class QueryValue<T> : QueryData<T>() {
    abstract val propertyName: String
    abstract val value: T
}

data class QueryString(
    override val propertyName: String,
    override val domainTag: String,
    override val value: String
) : QueryValue<String>()

data class QueryBoolean(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Boolean
) : QueryValue<Boolean>()

data class QueryDouble(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Double
) : QueryValue<Double>()

data class QueryLong(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Long
) : QueryValue<Long>()

data class QueryInstant(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Instant
) : QueryValue<Instant>()