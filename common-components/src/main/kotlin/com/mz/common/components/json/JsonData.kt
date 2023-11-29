package com.mz.common.components.json

typealias JsonData = Map<String, Any?>

inline fun <reified T> JsonData.requiredAtr(key: String): T {
    val value = get(key) ?: error("Missing required attribute $key")
    return value as? T ?: error("Attribute $key is not of type ${T::class}")
}

inline fun <reified T> JsonData.requiredAtr(key: String, f: (String) -> T): T {
    val value = get(key) ?: error("Missing required attribute $key")
    return f(value.toString())
}

inline fun <reified T> JsonData.attribute(key: String): T? {
    return get(key) as? T
}

inline fun <reified T> JsonData.attribute(key: String, f: (String) -> T): T? {
    return get(key)?.let { f(it.toString()) }
}

