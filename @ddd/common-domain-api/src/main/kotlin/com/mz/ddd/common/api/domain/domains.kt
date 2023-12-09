package com.mz.ddd.common.api.domain

import kotlinx.datetime.toKotlinInstant
import java.util.*

val uuid = { UUID.randomUUID().toString() }

val newId = { Id(uuid()) }

val instantNow = { java.time.Instant.now().toKotlinInstant() }

/**
 * Domain entity related tag, usually it is some Aggregate.
 */
data class DomainTag(val value: String) {
    init {
        value.validateNotBlank()
    }
}