package com.mz.ddd.common.api.domain

import kotlinx.serialization.Serializable

@Serializable
abstract class Aggregate {
    abstract val aggregateId: Id
}