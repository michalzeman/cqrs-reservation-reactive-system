package com.mz.ddd.common.api.util

sealed class Try<T> {

    companion object {
        operator fun <T> invoke(execute: () -> T): Try<T> {
            return try {
                Success(execute())
            } catch (exc: Exception) {
                Failure(exc)
            }
        }
    }

    abstract fun isSuccess(): Boolean

    abstract fun isFailure(): Boolean

    fun <O> map(f: (T) -> O): Try<O> {
        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is Success -> Try {
                f(this.result)
            }

            is Failure -> this as Failure<O>
        }
    }

    fun <O> flatMap(f: (T) -> Try<O>): Try<O> {
        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is Success -> f(this.result)
            is Failure -> this as Failure<O>
        }
    }

    fun get(): T {
        return when (this) {
            is Success -> result
            is Failure -> throw exc
        }
    }

    fun getOrElse(default: T): T {
        return when (this) {
            is Success -> result
            is Failure -> default
        }
    }

    fun orElse(default: () -> T): T {
        return when (this) {
            is Success -> result
            is Failure -> default()
        }
    }

}

data class Success<T>(val result: T) : Try<T>() {
    override fun isSuccess() = true

    override fun isFailure() = false
}

data class Failure<T>(val exc: Throwable) : Try<T>() {
    override fun isSuccess() = false

    override fun isFailure() = true
}
