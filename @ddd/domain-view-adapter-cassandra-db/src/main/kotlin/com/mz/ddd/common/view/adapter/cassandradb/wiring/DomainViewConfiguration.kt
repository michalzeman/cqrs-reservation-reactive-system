package com.mz.ddd.common.view.adapter.cassandradb.wiring

import com.mz.ddd.common.view.adapter.cassandradb.internal.readonly.QueryableTextViewEntityRepository
import com.mz.ddd.common.view.adapter.cassandradb.internal.save.QueryableStringEntityRepository
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@ComponentScan("com.mz.ddd.common.view.**")
@EnableReactiveCassandraRepositories(
    basePackageClasses = [
        QueryableStringEntityRepository::class,
        QueryableTextViewEntityRepository::class
    ]
)
class DomainViewConfiguration {
}