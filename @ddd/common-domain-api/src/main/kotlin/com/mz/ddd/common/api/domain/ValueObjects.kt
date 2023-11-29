package com.mz.ddd.common.api.domain

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Id(val value: String) {
    init {
        value.validateNotBlank()
    }
}

@JvmInline
@Serializable
value class Version(val value: Long = 0) {
    init {
        assert(value >= 0)
    }

    fun increment(): Version = Version(value.inc())
}

@JvmInline
@Serializable
value class LastName(val value: String) : CharSequence by value {
    init {
        value.validateNotBlank()
    }
}

@JvmInline
@Serializable
value class FirstName(val value: String) : CharSequence by value {
    init {
        value.validateNotBlank()
    }
}

@Serializable
data class Name(val lastName: LastName, val firstName: FirstName)

@JvmInline
@Serializable
value class Email(val value: String) : CharSequence by value {
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