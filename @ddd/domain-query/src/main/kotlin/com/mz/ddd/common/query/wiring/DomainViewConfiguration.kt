package com.mz.ddd.common.query.wiring

import com.mz.ddd.common.query.internal.readonly.QueryableTextViewEntityRepository
import com.mz.ddd.common.query.internal.save.QueryableStringEntityRepository
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@ComponentScan("com.mz.ddd.common.query.**")
@EnableReactiveCassandraRepositories(
    basePackageClasses = [
        QueryableStringEntityRepository::class,
        QueryableTextViewEntityRepository::class
    ]
)
class DomainViewConfiguration {
}