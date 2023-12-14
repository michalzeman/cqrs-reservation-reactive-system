package com.mz.ddd.common.query

import reactor.core.publisher.Mono

interface DomainViewRepository {

    fun save(queryableData: Set<QueryableData<*>>): Mono<Void>
}