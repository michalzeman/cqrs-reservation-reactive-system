package com.mz.ddd.common.query.internal.save

import com.mz.ddd.common.query.DomainViewRepository
import com.mz.ddd.common.query.QueryableBoolean
import com.mz.ddd.common.query.QueryableData
import com.mz.ddd.common.query.QueryableDouble
import com.mz.ddd.common.query.QueryableInstant
import com.mz.ddd.common.query.QueryableLong
import com.mz.ddd.common.query.QueryableString
import com.mz.ddd.common.query.toEntity
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
    override fun save(queryableData: Set<QueryableData<*>>): Mono<Void> {
        val saveOperations = queryableData.map { save(it) }
        return Flux.merge(saveOperations).then()
    }

    private fun save(queryableData: QueryableData<*>): Mono<Void> {
        return when (queryableData) {
            is QueryableString -> queryableStringEntityRepository.save(queryableData.toEntity()).then()
            is QueryableBoolean -> queryableBooleanEntityRepository.save(queryableData.toEntity()).then()
            is QueryableDouble -> queryableDoubleEntityRepository.save(queryableData.toEntity()).then()
            is QueryableInstant -> queryableInstantEntityRepository.save(queryableData.toEntity()).then()
            is QueryableLong -> queryableLongEntityRepository.save(queryableData.toEntity()).then()
        }
    }
}