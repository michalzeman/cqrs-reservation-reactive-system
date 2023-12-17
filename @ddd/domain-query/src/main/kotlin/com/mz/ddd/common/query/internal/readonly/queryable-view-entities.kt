package com.mz.ddd.common.query.internal.readonly

import com.mz.ddd.common.query.QueryableBoolean
import com.mz.ddd.common.query.QueryableDouble
import com.mz.ddd.common.query.QueryableInstant
import com.mz.ddd.common.query.QueryableLong
import com.mz.ddd.common.query.QueryableString
import kotlinx.datetime.toKotlinInstant
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant


/*
(property_name, domain_tag, value)
 */

@Table("queryable_text_view")
internal class QueryableTextViewEntity {
    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.CLUSTERED)
    var aggregateId: String? = null

    @PrimaryKeyColumn(name = "property_name", type = PrimaryKeyType.PARTITIONED)
    var propertyName: String? = null

    @PrimaryKeyColumn(name = "value", type = PrimaryKeyType.PARTITIONED)
    var value: String? = null

    @PrimaryKeyColumn(name = "domain_tag", type = PrimaryKeyType.PARTITIONED)
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_boolean_view")
internal class QueryableBooleanViewEntity {
    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.CLUSTERED)
    var aggregateId: String? = null

    @PrimaryKeyColumn(name = "property_name", type = PrimaryKeyType.PARTITIONED)
    var propertyName: String? = null

    @PrimaryKeyColumn(name = "value", type = PrimaryKeyType.PARTITIONED)
    var value: Boolean? = null

    @PrimaryKeyColumn(name = "domain_tag", type = PrimaryKeyType.PARTITIONED)
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_timestamp_view")
internal class QueryableTimestampViewEntity {
    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.CLUSTERED)
    var aggregateId: String? = null

    @PrimaryKeyColumn(name = "property_name", type = PrimaryKeyType.PARTITIONED)
    var propertyName: String? = null

    @PrimaryKeyColumn(name = "value", type = PrimaryKeyType.PARTITIONED)
    var value: Instant? = null

    @PrimaryKeyColumn(name = "domain_tag", type = PrimaryKeyType.PARTITIONED)
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_long_view")
internal class QueryableLongViewEntity {
    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.CLUSTERED)
    var aggregateId: String? = null

    @PrimaryKeyColumn(name = "property_name", type = PrimaryKeyType.PARTITIONED)
    var propertyName: String? = null

    @PrimaryKeyColumn(name = "value", type = PrimaryKeyType.PARTITIONED)
    var value: Long? = null

    @PrimaryKeyColumn(name = "domain_tag", type = PrimaryKeyType.PARTITIONED)
    var domainTag: String? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_double_view")
internal class QueryableDoubleViewEntity {
    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.CLUSTERED)
    var aggregateId: String? = null

    @PrimaryKeyColumn(name = "property_name", type = PrimaryKeyType.PARTITIONED)
    var propertyName: String? = null

    @PrimaryKeyColumn(name = "value", type = PrimaryKeyType.PARTITIONED)
    var value: Double? = null

    @PrimaryKeyColumn(name = "domain_tag", type = PrimaryKeyType.PARTITIONED)
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