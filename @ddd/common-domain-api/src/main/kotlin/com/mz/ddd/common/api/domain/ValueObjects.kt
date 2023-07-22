package com.mz.ddd.common.api.domain

import kotlinx.serialization.Serializable

@Serializable
data class Id(val value: String) {
    init {
        value.validateValue()
    }
}

@Serializable
data class Version(val value: Long = 0) {
    init {
        assert(value >= 0)
    }

    fun increment(): Version = Version(value.inc())
}

@Serializable
data class LastName(val value: String) {
    init {
        value.validateValue()
    }
}

@Serializable
data class FirstName(val value: String) {
    init {
        value.validateValue()
    }
}

@Serializable
data class Email(val value: String) {
    init {
        value.validateValue()
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        assert(emailRegex.matches(value))
    }
}

fun String.validateValue() {
    assert(this.isNotBlank())
}