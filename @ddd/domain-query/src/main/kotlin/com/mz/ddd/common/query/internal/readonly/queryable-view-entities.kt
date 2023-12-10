package com.mz.ddd.common.query.internal.readonly

import com.mz.ddd.common.query.*
import kotlinx.datetime.toKotlinInstant
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("queryable_text_view")
internal class QueryableTextViewEntity {
    @Column("aggregate_id")
    var aggregateId: UUID? = null

    @Column("property_name")
    var propertyName: String? = null

    @Column("value")
    var value: String? = null

    @Column("domain_tag")
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_boolean_view")
internal class QueryableBooleanViewEntity {
    @Column("aggregate_id")
    var aggregateId: UUID? = null

    @Column("property_name")
    var propertyName: String? = null

    @Column("value")
    var value: Boolean? = null

    @Column("domain_tag")
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_timestamp_view")
internal class QueryableTimestampViewEntity {
    @Column("aggregate_id")
    var aggregateId: UUID? = null

    @Column("property_name")
    var propertyName: String? = null

    @Column("value")
    var value: Instant? = null

    @Column("domain_tag")
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_long_view")
internal class QueryableLongViewEntity {
    @Column("aggregate_id")
    var aggregateId: UUID? = null

    @Column("property_name")
    var propertyName: String? = null

    @Column("value")
    var value: Long? = null

    @Column("domain_tag")
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_double_view")
internal class QueryableDoubleViewEntity {
    @Column("aggregate_id")
    var aggregateId: UUID? = null

    @Column("property_name")
    var propertyName: String? = null

    @Column("value")
    var value: Double? = null

    @Column("domain_tag")
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

internal fun QueryableTextViewEntity.toDataClass(): QueryableString {
    return QueryableString(
        aggregateId = this.aggregateId.toString(),
        propertyName = this.propertyName!!,
        domainTag = this.domainTag!!,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}

internal fun QueryableBooleanViewEntity.toDataClass(): QueryableBoolean {
    return QueryableBoolean(
        aggregateId = this.aggregateId.toString(),
        propertyName = this.propertyName!!,
        domainTag = this.domainTag!!,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}

internal fun QueryableTimestampViewEntity.toDataClass(): QueryableInstant {
    return QueryableInstant(
        aggregateId = this.aggregateId.toString(),
        propertyName = this.propertyName!!,
        domainTag = this.domainTag!!,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!.toKotlinInstant()
    )
}

internal fun QueryableLongViewEntity.toDataClass(): QueryableLong {
    return QueryableLong(
        aggregateId = this.aggregateId.toString(),
        propertyName = this.propertyName!!,
        domainTag = this.domainTag!!,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}

internal fun QueryableDoubleViewEntity.toDataClass(): QueryableDouble {
    return QueryableDouble(
        aggregateId = this.aggregateId.toString(),
        propertyName = this.propertyName!!,
        domainTag = this.domainTag!!,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}