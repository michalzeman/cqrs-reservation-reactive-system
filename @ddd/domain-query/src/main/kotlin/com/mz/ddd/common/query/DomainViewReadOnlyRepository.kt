package com.mz.ddd.common.query

import com.mz.ddd.common.api.domain.Id
import reactor.core.publisher.Flux

data class DomainView(
    val aggregateId: Id,
    val views: Set<QueryableData<*>>
)

interface DomainViewReadOnlyRepository {
    fun find(queryOperation: QueryOperation): Flux<DomainView>
}