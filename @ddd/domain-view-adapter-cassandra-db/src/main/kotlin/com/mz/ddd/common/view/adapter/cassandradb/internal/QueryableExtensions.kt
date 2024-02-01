package com.mz.ddd.common.view.adapter.cassandradb.internal

import com.mz.ddd.common.view.QueryableBoolean
import com.mz.ddd.common.view.QueryableDouble
import com.mz.ddd.common.view.QueryableInstant
import com.mz.ddd.common.view.QueryableLong
import com.mz.ddd.common.view.QueryableString
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableBooleanEntity
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableDoubleEntity
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableInstantEntity
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableKey
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableLongEntity
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableStringEntity
import kotlinx.datetime.toJavaInstant

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