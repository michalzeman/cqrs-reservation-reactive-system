package com.mz.ddd.common.query

import com.mz.ddd.common.query.internal.save.QueryableBooleanEntity
import com.mz.ddd.common.query.internal.save.QueryableDoubleEntity
import com.mz.ddd.common.query.internal.save.QueryableInstantEntity
import com.mz.ddd.common.query.internal.save.QueryableKey
import com.mz.ddd.common.query.internal.save.QueryableLongEntity
import com.mz.ddd.common.query.internal.save.QueryableStringEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

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

internal fun QueryableString.toEntity(): QueryableStringEntity {
    return QueryableStringEntity().apply {
        key = QueryableKey(propertyName, domainTag, aggregateId)
        timestamp = this@toEntity.timestamp.toJavaInstant()
        value = this@toEntity.value
    }
}

internal fun QueryableBoolean.toEntity(): QueryableBooleanEntity {
    return QueryableBooleanEntity().apply {
        key = QueryableKey(propertyName, domainTag, aggregateId)
        timestamp = this@toEntity.timestamp.toJavaInstant()
        value = this@toEntity.value
    }
}

internal fun QueryableInstant.toEntity(): QueryableInstantEntity {
    return QueryableInstantEntity().apply {
        key = QueryableKey(propertyName, domainTag, aggregateId)
        timestamp = this@toEntity.timestamp.toJavaInstant()
        value = this@toEntity.value.toJavaInstant()
    }
}

internal fun QueryableLong.toEntity(): QueryableLongEntity {
    return QueryableLongEntity().apply {
        key = QueryableKey(propertyName, domainTag, aggregateId)
        timestamp = this@toEntity.timestamp.toJavaInstant()
        value = this@toEntity.value
    }
}

internal fun QueryableDouble.toEntity(): QueryableDoubleEntity {
    return QueryableDoubleEntity().apply {
        key = QueryableKey(propertyName, domainTag, aggregateId)
        timestamp = this@toEntity.timestamp.toJavaInstant()
        value = this@toEntity.value
    }
}

