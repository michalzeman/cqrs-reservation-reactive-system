package com.mz.ddd.common.view.adapter.cassandradb.internal.save

import com.mz.ddd.common.view.QueryableBoolean
import com.mz.ddd.common.view.QueryableDouble
import com.mz.ddd.common.view.QueryableInstant
import com.mz.ddd.common.view.QueryableLong
import com.mz.ddd.common.view.QueryableString
import kotlinx.datetime.toKotlinInstant
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
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

internal abstract class AbstractQueryableEntity {
    @PrimaryKey
    var key: QueryableKey? = null

    @Column("timestamp")
    var timestamp: Instant? = null
}

@Table("queryable_text")
internal class QueryableStringEntity : AbstractQueryableEntity() {
    @Column("value")
    var value: String? = null
}

@Table("queryable_boolean")
internal class QueryableBooleanEntity : AbstractQueryableEntity() {
    @Column("value")
    var value: Boolean? = null
}

@Table("queryable_timestamp")
internal class QueryableInstantEntity : AbstractQueryableEntity() {
    @Column("value")
    var value: Instant? = null
}

@Table("queryable_long")
internal class QueryableLongEntity : AbstractQueryableEntity() {
    @Column("value")
    var value: Long? = null
}

@Table("queryable_double")
internal class QueryableDoubleEntity : AbstractQueryableEntity() {
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
