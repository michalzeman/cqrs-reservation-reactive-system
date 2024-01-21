package com.mz.ddd.common.view

import kotlinx.datetime.Instant

enum class OperationType {
    AND, OR, LIKE, NOT, EQUALS, GREATER_THAN, LESS_THAN
}

sealed interface QueryOperation

data class DomainViewQuery(
    val queryableDataSet: Set<QueryData<*>>,
    val operationType: OperationType? = null
) : QueryOperation

data class BetweenInstantQuery(
    val startTimePropertyName: String,
    val endTimePropertyName: String,
    val domainTag: String,
    val startTime: Instant,
    val endTime: Instant
) : QueryOperation

sealed class QueryData<T> {
    abstract val propertyName: String
    abstract val domainTag: String
    abstract val value: T
}

data class QueryString(
    override val propertyName: String,
    override val domainTag: String,
    override val value: String
) : QueryData<String>()

data class QueryBoolean(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Boolean
) : QueryData<Boolean>()

data class QueryDouble(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Double
) : QueryData<Double>()

data class QueryLong(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Long
) : QueryData<Long>()

data class QueryInstant(
    override val propertyName: String,
    override val domainTag: String,
    override val value: Instant
) : QueryData<Instant>()