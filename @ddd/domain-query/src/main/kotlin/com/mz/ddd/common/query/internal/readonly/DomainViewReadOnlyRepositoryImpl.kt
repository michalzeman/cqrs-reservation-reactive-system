package com.mz.ddd.common.query.internal.readonly

import com.mz.ddd.common.query.DomainView
import com.mz.ddd.common.query.DomainViewReadOnlyRepository
import com.mz.ddd.common.query.QueryOperation
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
internal class DomainViewReadOnlyRepositoryImpl : DomainViewReadOnlyRepository {
    override fun find(queryOperation: QueryOperation): Flux<DomainView> {
        TODO("Not yet implemented")
    }
}