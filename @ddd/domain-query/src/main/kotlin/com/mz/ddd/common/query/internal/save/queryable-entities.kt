package com.mz.ddd.common.query.internal.save

import com.mz.ddd.common.query.*
import kotlinx.datetime.toKotlinInstant
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.*
import java.time.Instant

@PrimaryKeyClass
internal data class QueryableKey(
    @PrimaryKeyColumn(name = "property_name", type = PrimaryKeyType.PARTITIONED)
    val propertyName: String,

    @PrimaryKeyColumn(name = "domain_tag", type = PrimaryKeyType.PARTITIONED)
    val domainTag: String,

    @PrimaryKeyColumn(name = "aggregate_id", type = PrimaryKeyType.CLUSTERED)
    val aggregateId: String
)

@Table("queryable_text")
internal class QueryableStringEntity {
    @PrimaryKey
    var key: QueryableKey? = null

    @Column("timestamp")
    var timestamp: Instant? = null

    @Column("value")
    var value: String? = null
}

@Table("queryable_boolean")
internal class QueryableBooleanEntity {
    @PrimaryKey
    var key: QueryableKey? = null

    @Column("timestamp")
    var timestamp: Instant? = null

    @Column("value")
    var value: Boolean? = null
}

@Table("queryable_timestamp")
internal class QueryableInstantEntity {
    @PrimaryKey
    var key: QueryableKey? = null

    @Column("timestamp")
    var timestamp: Instant? = null

    @Column("value")
    var value: Instant? = null
}

@Table("queryable_long")
internal class QueryableLongEntity {
    @PrimaryKey
    var key: QueryableKey? = null

    @Column("timestamp")
    var timestamp: Instant? = null

    @Column("value")
    var value: Long? = null
}

@Table("queryable_double")
internal class QueryableDoubleEntity {
    @PrimaryKey
    var key: QueryableKey? = null

    @Column("timestamp")
    var timestamp: Instant? = null

    @Column("value")
    var value: Double? = null
}

internal fun QueryableStringEntity.toQueryableString(): QueryableString {
    return QueryableString(
        aggregateId = this.key!!.aggregateId,
        propertyName = this.key!!.propertyName,
        domainTag = this.key!!.domainTag,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}

internal fun QueryableBooleanEntity.toQueryableBoolean(): QueryableBoolean {
    return QueryableBoolean(
        aggregateId = this.key!!.aggregateId,
        propertyName = this.key!!.propertyName,
        domainTag = this.key!!.domainTag,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}

internal fun QueryableInstantEntity.toQueryableInstant(): QueryableInstant {
    return QueryableInstant(
        aggregateId = this.key!!.aggregateId,
        propertyName = this.key!!.propertyName,
        domainTag = this.key!!.domainTag,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!.toKotlinInstant()
    )
}

internal fun QueryableLongEntity.toQueryableLong(): QueryableLong {
    return QueryableLong(
        aggregateId = this.key!!.aggregateId,
        propertyName = this.key!!.propertyName,
        domainTag = this.key!!.domainTag,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}

internal fun QueryableDoubleEntity.toQueryableDouble(): QueryableDouble {
    return QueryableDouble(
        aggregateId = this.key!!.aggregateId,
        propertyName = this.key!!.propertyName,
        domainTag = this.key!!.domainTag,
        timestamp = this.timestamp!!.toKotlinInstant(),
        value = this.value!!
    )
}
