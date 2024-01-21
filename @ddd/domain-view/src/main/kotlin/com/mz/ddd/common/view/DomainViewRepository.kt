package com.mz.ddd.common.view

import reactor.core.publisher.Mono

interface DomainViewRepository {

    fun save(queryableData: Set<QueryableData<*>>): Mono<Void>
}