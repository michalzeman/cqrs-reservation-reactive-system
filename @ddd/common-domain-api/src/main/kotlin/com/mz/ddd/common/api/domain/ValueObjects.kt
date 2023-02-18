package com.mz.ddd.common.api.domain

data class Id(val value: String)

data class Version(val value: Long = 0) {
    fun increment(): Version = Version(value.inc())
}

data class LastName(val value: String) {
    init {
        value.validateValue()
    }
}

data class FirstName(val value: String) {
    init {
        value.validateValue()
    }
}

data class Email(val value: String)

fun String.validateValue() {
    validatedString(this)
}

private fun validatedString(value: String?): String {
    val result = value!!
    assert(result.isNotBlank())
    return result
}