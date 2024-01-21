package com.mz.ddd.common.view.adapter.cassandradb.internal.save

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface QueryableStringEntityRepository : ReactiveCrudRepository<QueryableStringEntity, QueryableKey>

@Repository
internal interface QueryableBooleanEntityRepository : ReactiveCrudRepository<QueryableBooleanEntity, QueryableKey>

@Repository
internal interface QueryableInstantEntityRepository : ReactiveCrudRepository<QueryableInstantEntity, QueryableKey>

@Repository
internal interface QueryableLongEntityRepository : ReactiveCrudRepository<QueryableLongEntity, QueryableKey>

@Repository
internal interface QueryableDoubleEntityRepository : ReactiveCrudRepository<QueryableDoubleEntity, QueryableKey>