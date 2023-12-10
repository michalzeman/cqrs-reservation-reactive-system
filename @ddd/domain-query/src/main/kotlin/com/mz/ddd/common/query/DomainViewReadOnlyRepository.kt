package com.mz.ddd.common.query

import reactor.core.publisher.Flux

data class DomainView(
    val views: Set<QueryableView<*>>
)

interface DomainViewReadOnlyRepository {
    fun find(queryOperation: QueryOperation): Flux<DomainView>
}