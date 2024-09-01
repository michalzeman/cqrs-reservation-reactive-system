package com.mz.common.components.json

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Kotlin native supported JSON serialization
 */
inline fun <reified T> serToJsonString(value: T) = Json.encodeToString<T>(value)

/**
 * Kotlin native supported JSON deserialization
 */
inline fun <reified T> desJson(value: String): T = Json.decodeFromString<T>(value)