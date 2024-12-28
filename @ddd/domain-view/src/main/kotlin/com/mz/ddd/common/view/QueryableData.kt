package com.mz.ddd.common.view

import kotlinx.datetime.Instant

sealed class QueryableData<T> {
    abstract val aggregateId: String
    abstract val propertyName: String
    abstract val domainTag: String
    abstract val timestamp: Instant
    abstract val value: T
}

data class QueryableString(
    override val aggregateId: String,
    override val propertyName: String,
    override val domainTag: String,
    override val timestamp: Instant,
    override val value: String
) : QueryableData<String>()

data class QueryableBoolean(
    override val aggregateId: String,
    override val propertyName: String,
    override val domainTag: String,
    override val timestamp: Instant,
    override val value: Boolean
) : QueryableData<Boolean>()

data class QueryableInstant(
    override val aggregateId: String,
    override val propertyName: String,
    override val domainTag: String,
    override val timestamp: Instant,
    override val value: Instant
) : QueryableData<Instant>()

data class QueryableBetweenInstant(
    override val aggregateId: String,
    override val propertyName: String,
    override val domainTag: String,
    override val timestamp: Instant,
    override val value: Set<QueryableData<Instant>>
) : QueryableData<Set<QueryableData<Instant>>>()

data class QueryableLong(
    override val aggregateId: String,
    override val propertyName: String,
    override val domainTag: String,
    override val timestamp: Instant,
    override val value: Long
) : QueryableData<Long>()

data class QueryableDouble(
    override val aggregateId: String,
    override val propertyName: String,
    override val domainTag: String,
    override val timestamp: Instant,
    override val value: Double
) : QueryableData<Double>()
