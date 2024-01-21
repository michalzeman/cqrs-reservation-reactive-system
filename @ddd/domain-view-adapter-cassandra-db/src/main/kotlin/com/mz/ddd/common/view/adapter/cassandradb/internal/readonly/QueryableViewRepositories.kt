package com.mz.ddd.common.view.adapter.cassandradb.internal.readonly

import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.Instant

@NoRepositoryBean
internal interface BaseQueryableViewEntityRepository<VALUE, ENTITY, ID> : CrudRepository<ENTITY, ID> {
    fun findByPropertyNameAndDomainTagAndValue(
        propertyName: String,
        domainTag: String,
        value: VALUE
    ): Flux<ENTITY>
}

@Repository
internal interface QueryableTextViewEntityRepository :
    BaseQueryableViewEntityRepository<String, QueryableTextViewEntity, String>

@Repository
internal interface QueryableBooleanViewEntityRepository :
    BaseQueryableViewEntityRepository<Boolean, QueryableBooleanViewEntity, String>

@Repository
internal interface QueryableTimestampViewEntityRepository :
    BaseQueryableViewEntityRepository<Instant, QueryableTimestampViewEntity, String> {

    @Query(
        "SELECT * FROM queryable_timestamp_view " +
                "WHERE property_name = ?0 " +
                "AND domain_tag = ?1 " +
                "AND value >= ?2 " +
                "AND value <= ?3 ALLOW FILTERING"
    ) // Not ideal solution :(
    fun findByPropertyNameAndDomainTagAndValueBetween(
        propertyName: String,
        domainTag: String,
        value1: Instant,
        value2: Instant
    ): Flux<QueryableTimestampViewEntity>

    @AllowFiltering
    fun findByPropertyNameAndDomainTagAndValueBefore(
        propertyName: String,
        domainTag: String,
        value: Instant
    ): Flux<QueryableTimestampViewEntity>
}

@Repository
internal interface QueryableLongViewEntityRepository :
    BaseQueryableViewEntityRepository<Long, QueryableLongViewEntity, String>

@Repository
internal interface QueryableDoubleViewEntityRepository :
    BaseQueryableViewEntityRepository<Double, QueryableDoubleViewEntity, String>