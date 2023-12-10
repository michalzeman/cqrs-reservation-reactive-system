package com.mz.ddd.common.query

enum class OperationType {
    AND, OR, LIKE, NOT, EQUALS, GREATER_THAN, LESS_THAN
}

data class DomainViewQuery(
    val queryableViewSet: Set<QueryableView<*>>,
    val operationType: OperationType? = null
)

data class QueryOperation(
    val views: Set<DomainViewQuery>
)