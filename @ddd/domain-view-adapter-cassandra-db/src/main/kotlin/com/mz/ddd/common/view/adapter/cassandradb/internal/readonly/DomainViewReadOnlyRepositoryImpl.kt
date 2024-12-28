package com.mz.ddd.common.view.adapter.cassandradb.internal.readonly

import com.mz.ddd.common.api.domain.DomainTag
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.view.*
import com.mz.ddd.common.view.OperationType.AND
import kotlinx.datetime.toJavaInstant
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

@Component
internal class DomainViewReadOnlyRepositoryImpl(
    val queryableTextViewEntityRepository: QueryableTextViewEntityRepository,
    val queryableBooleanViewEntityRepository: QueryableBooleanViewEntityRepository,
    val queryableTimestampViewEntityRepository: QueryableTimestampViewEntityRepository,
    val queryableLongViewEntityRepository: QueryableLongViewEntityRepository,
    val queryableDoubleViewEntityRepository: QueryableDoubleViewEntityRepository
) : DomainViewReadOnlyRepository {
    override fun find(queryOperation: QueryOperation): Flux<DomainView> {
        return when (queryOperation) {
            is DomainViewQuery -> domainViewQuery(queryOperation)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    private fun domainViewQuery(query: DomainViewQuery): Flux<DomainView> {
        val resultStream = Flux.fromIterable(query.queryableDataSet)
            .flatMap { queryableView(it) }
            .groupBy { it.aggregateId }
            .flatMap { group ->
                group.collectList()
                    .map { DomainView(Id(group.key()), it.toSet()) }
            }.cache()

        return if (query.operationType == AND) {
            resultStream.filter {
                it.views.size == query.queryableDataSet.size
            }
        } else resultStream
    }

    private fun queryableView(queryValue: QueryData<*>): Flux<QueryableData<*>> {
        return when (queryValue) {
            is QueryBoolean -> queryableBooleanViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryValue.propertyName,
                domainTag = queryValue.domainTag,
                value = queryValue.value
            ).map { it.toDataClass() }

            is QueryDouble -> queryableDoubleViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryValue.propertyName,
                domainTag = queryValue.domainTag,
                value = queryValue.value
            ).map { it.toDataClass() }

            is QueryInstant -> queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryValue.propertyName,
                domainTag = queryValue.domainTag,
                value = queryValue.value.toJavaInstant()
            ).map { it.toDataClass() }

            is QueryLong -> queryableLongViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryValue.propertyName,
                domainTag = queryValue.domainTag,
                value = queryValue.value
            ).map { it.toDataClass() }

            is QueryString -> queryableTextViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryValue.propertyName,
                domainTag = queryValue.domainTag,
                value = queryValue.value
            ).map { it.toDataClass() }

            is BetweenInstantQuery -> betweenInstantQuery(queryValue)
        }
    }

    private fun betweenInstantQuery(query: BetweenInstantQuery): Flux<QueryableData<*>> {
        val byStart = queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValueBetween(
            propertyName = query.startTimePropertyName,
            domainTag = query.domainTag,
            value1 = query.startTime.toJavaInstant(),
            value2 = query.endTime.toJavaInstant()
        )
        val byEnd = queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValueBetween(
            propertyName = query.endTimePropertyName,
            domainTag = query.domainTag,
            value1 = query.startTime.toJavaInstant(),
            value2 = query.endTime.toJavaInstant()
        )

        return Flux.merge(byStart, byEnd)
            .map { it.toDataClass() }
            .groupBy { it.aggregateId }
            .flatMap { group ->
                group.collectList()
                    .map {
                        val defaultValue = it[0]
                        QueryableBetweenInstant(
                            group.key(),
                            "timeBetween",
                            defaultValue.domainTag,
                            defaultValue.timestamp,
                            it.toSet()
                        )
                    }
            }
    }
}