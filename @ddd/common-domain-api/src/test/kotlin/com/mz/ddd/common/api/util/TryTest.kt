package com.mz.ddd.common.api.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class TryTest {

    @Test
    internal fun try_Success() {
        val result = Try { 4 }

        assertThat(result.isSuccess()).isTrue
        assertThat(result.isFailure()).isFalse
        assertThat(result is Success).isTrue
        assertThat(result.map { it * 2 }).isEqualTo(Success(8))
        assertThat(result.flatMap { Try { "Success ${it * 2}" } }).isEqualTo(Success("Success 8"))
    }

    @Test
    internal fun try_Failure() {
        val exc = RuntimeException("Bom!")
        val result = Try<Int> { throw exc }

        assertThat(result.isSuccess()).isFalse
        assertThat(result.isFailure()).isTrue
        assertThat(result is Failure).isTrue
        assertThat(result.map { it * 2 }).isEqualTo(Failure<Int>(exc))
        assertThat(result.flatMap { Try { "Success ${it * 2}" } }).isEqualTo(Failure<String>(exc))
        assertThat(result.getOrElse(10)).isEqualTo(10)
        assertThat(result.orElse { 10 }).isEqualTo(10)
        assertThrows<java.lang.RuntimeException> { result.get() }
    }
}