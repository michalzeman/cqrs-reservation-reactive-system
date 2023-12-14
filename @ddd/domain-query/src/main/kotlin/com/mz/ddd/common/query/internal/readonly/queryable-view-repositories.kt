package com.mz.ddd.common.query.internal.readonly

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.Instant

@Repository
internal interface QueryableTextViewEntityRepository : ReactiveCrudRepository<QueryableTextViewEntity, String> {
    fun findByPropertyNameAndDomainTagAndValue(
        propertyName: String,
        domainTag: String,
        value: String
    ): Flux<QueryableTextViewEntity>
}

@Repository
internal interface QueryableBooleanViewEntityRepository : ReactiveCrudRepository<QueryableBooleanViewEntity, String> {
    fun findByPropertyNameAndDomainTagAndValue(
        propertyName: String,
        domainTag: String,
        value: Boolean
    ): Flux<QueryableBooleanViewEntity>
}

@Repository
internal interface QueryableTimestampViewEntityRepository :
    ReactiveCrudRepository<QueryableTimestampViewEntity, String> {
    fun findByPropertyNameAndDomainTagAndValue(
        propertyName: String,
        domainTag: String,
        value: Instant
    ): Flux<QueryableTimestampViewEntity>
}

@Repository
internal interface QueryableLongViewEntityRepository : ReactiveCrudRepository<QueryableLongViewEntity, String> {
    fun findByPropertyNameAndDomainTagAndValue(
        propertyName: String,
        domainTag: String,
        value: Long
    ): Flux<QueryableLongViewEntity>
}

@Repository
internal interface QueryableDoubleViewEntityRepository : ReactiveCrudRepository<QueryableDoubleViewEntity, String> {
    fun findByPropertyNameAndDomainTagAndValue(
        propertyName: String,
        domainTag: String,
        value: Double
    ): Flux<QueryableDoubleViewEntity>
}