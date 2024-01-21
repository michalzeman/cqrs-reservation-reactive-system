package com.mz.ddd.common.view.adapter.cassandradb.internal.readonly

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.view.BetweenInstantQuery
import com.mz.ddd.common.view.DomainView
import com.mz.ddd.common.view.DomainViewQuery
import com.mz.ddd.common.view.DomainViewReadOnlyRepository
import com.mz.ddd.common.view.QueryBoolean
import com.mz.ddd.common.view.QueryData
import com.mz.ddd.common.view.QueryDouble
import com.mz.ddd.common.view.QueryInstant
import com.mz.ddd.common.view.QueryLong
import com.mz.ddd.common.view.QueryOperation
import com.mz.ddd.common.view.QueryString
import com.mz.ddd.common.view.QueryableData
import kotlinx.datetime.toJavaInstant
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

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
            is BetweenInstantQuery -> betweenInstantQuery(queryOperation)
        }
    }

    private fun domainViewQuery(query: DomainViewQuery): Flux<DomainView> {
        return Flux.fromIterable(query.queryableDataSet)
            .flatMap { queryableView(it) }
            .collectList()
            .map { views ->
                views.groupBy { it.aggregateId }
            }
            .flatMapIterable {
                it.entries
                    .map { entry -> DomainView(Id(entry.key), entry.value.toSet()) }
            }
            .filter { it.views.isNotEmpty() }
    }

    private fun queryableView(queryData: QueryData<*>): Flux<QueryableData<*>> {
        return when (queryData) {
            is QueryBoolean -> queryableBooleanViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryData.propertyName,
                domainTag = queryData.domainTag,
                value = queryData.value
            ).map { it.toDataClass() }

            is QueryDouble -> queryableDoubleViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryData.propertyName,
                domainTag = queryData.domainTag,
                value = queryData.value
            ).map { it.toDataClass() }

            is QueryInstant -> queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryData.propertyName,
                domainTag = queryData.domainTag,
                value = queryData.value.toJavaInstant()
            ).map { it.toDataClass() }

            is QueryLong -> queryableLongViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryData.propertyName,
                domainTag = queryData.domainTag,
                value = queryData.value
            ).map { it.toDataClass() }

            is QueryString -> queryableTextViewEntityRepository.findByPropertyNameAndDomainTagAndValue(
                propertyName = queryData.propertyName,
                domainTag = queryData.domainTag,
                value = queryData.value
            ).map { it.toDataClass() }

        }
    }

    private fun betweenInstantQuery(query: BetweenInstantQuery): Flux<DomainView> {
        val byStart = queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValueBetween(
            propertyName = query.startTimePropertyName,
            domainTag = query.domainTag,
            value1 = query.startTime.toJavaInstant(),
            value2 = query.endTime.toJavaInstant()
        ).map { it.toDataClass() }
        val byEnd = queryableTimestampViewEntityRepository.findByPropertyNameAndDomainTagAndValueBetween(
            propertyName = query.endTimePropertyName,
            domainTag = query.domainTag,
            value1 = query.startTime.toJavaInstant(),
            value2 = query.endTime.toJavaInstant()
        ).map { it.toDataClass() }
        return Flux.merge(byStart, byEnd)
            .collectList()
            .map { views ->
                views.groupBy { it.aggregateId }
            }
            .flatMapIterable {
                it.entries
                    .map { entry -> DomainView(Id(entry.key), entry.value.toSet()) }
            }
            .filter { it.views.isNotEmpty() }
    }
}