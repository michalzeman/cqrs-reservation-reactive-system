package com.mz.ddd.common.query.internal.readonly

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.query.DomainView
import com.mz.ddd.common.query.DomainViewQuery
import com.mz.ddd.common.query.DomainViewReadOnlyRepository
import com.mz.ddd.common.query.QueryBoolean
import com.mz.ddd.common.query.QueryData
import com.mz.ddd.common.query.QueryDouble
import com.mz.ddd.common.query.QueryInstant
import com.mz.ddd.common.query.QueryLong
import com.mz.ddd.common.query.QueryOperation
import com.mz.ddd.common.query.QueryString
import com.mz.ddd.common.query.QueryableData
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
}