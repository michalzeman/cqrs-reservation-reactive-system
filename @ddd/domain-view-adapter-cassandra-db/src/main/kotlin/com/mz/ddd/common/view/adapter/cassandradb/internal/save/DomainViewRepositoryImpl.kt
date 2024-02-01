package com.mz.ddd.common.view.adapter.cassandradb.internal.save

import com.mz.ddd.common.view.DomainViewRepository
import com.mz.ddd.common.view.QueryableBoolean
import com.mz.ddd.common.view.QueryableData
import com.mz.ddd.common.view.QueryableDouble
import com.mz.ddd.common.view.QueryableInstant
import com.mz.ddd.common.view.QueryableLong
import com.mz.ddd.common.view.QueryableString
import com.mz.ddd.common.view.adapter.cassandradb.internal.toEntity
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