package com.mz.reservation.common.api.domain

data class Id(val value: String)

data class Version(val value: Long = 0) {
    fun increment(): Version = Version(value.inc())
}

data class LastName(val value: String) {
    init {
        validatedString(value)
    }
}

data class FirstName(val value: String) {
    init {
        validatedString(value)
    }
}

data class Email(val value: String)

fun validatedString(value: String?): String {
    val result = value!!
    assert(result.isNotBlank())
    return result
}