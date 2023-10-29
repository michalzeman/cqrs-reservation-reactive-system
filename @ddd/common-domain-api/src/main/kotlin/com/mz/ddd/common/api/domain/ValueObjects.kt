package com.mz.ddd.common.api.domain

import kotlinx.serialization.Serializable

@Serializable
data class Id(val value: String) {
    init {
        value.validateNotBlank()
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
        value.validateNotBlank()
    }
}

@Serializable
data class FirstName(val value: String) {
    init {
        value.validateNotBlank()
    }
}

@Serializable
data class Name(val lastName: LastName, val firstName: FirstName)

@Serializable
data class Email(val value: String) {
    init {
        value.validateNotBlank()
        value.validateEmail()
    }
}

fun String.validateNotBlank() {
    assert(this.isNotBlank())
}

fun String.validateEmail() {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    assert(emailRegex.matches(this))
}