package com.mz.ddd.common.api.domain

import kotlinx.datetime.toKotlinInstant
import java.util.*

val uuid = { UUID.randomUUID().toString() }

val instantNow = { java.time.Instant.now().toKotlinInstant() }