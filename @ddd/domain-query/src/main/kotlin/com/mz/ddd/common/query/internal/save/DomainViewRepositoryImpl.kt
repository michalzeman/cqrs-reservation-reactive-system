package com.mz.ddd.common.query.internal.save

import com.mz.ddd.common.query.*
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
internal class DomainViewRepositoryImpl(
    val queryableStringEntityRepository: QueryableStringEntityRepository,
    val queryableBooleanEntityRepository: QueryableBooleanEntityRepository,
    val queryableInstantEntityRepository: QueryableInstantEntityRepository,
    val queryableLongEntityRepository: QueryableLongEntityRepository,
    val queryableDoubleEntityRepository: QueryableDoubleEntityRepository
) : DomainViewRepository {
    override fun save(queryableViews: Set<QueryableView<*>>): Mono<Void> {
        val saveOperations = queryableViews.map { save(it) }
        return Flux.merge(saveOperations).then()
    }

    private fun save(queryableView: QueryableView<*>): Mono<Void> {
        return when (queryableView) {
            is QueryableString -> queryableStringEntityRepository.save(queryableView.toEntity()).then()
            is QueryableBoolean -> queryableBooleanEntityRepository.save(queryableView.toEntity()).then()
            is QueryableDouble -> queryableDoubleEntityRepository.save(queryableView.toEntity()).then()
            is QueryableInstant -> queryableInstantEntityRepository.save(queryableView.toEntity()).then()
            is QueryableLong -> queryableLongEntityRepository.save(queryableView.toEntity()).then()
        }
    }
}