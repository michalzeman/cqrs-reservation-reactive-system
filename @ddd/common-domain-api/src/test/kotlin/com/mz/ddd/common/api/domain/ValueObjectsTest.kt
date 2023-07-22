package com.mz.ddd.common.api.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ValueObjectsTest {

    @Test
    fun `ID, when value is valid`() {
        val id = Id("123")
        assertThat(id.value).isEqualTo("123")
    }

    @Test
    fun `ID, when value empty or black string, then exception is thrown`() {
        assertThatThrownBy { Id(" ") }
            .isInstanceOf(AssertionError::class.java)

        assertThatThrownBy { Id("") }
    }

    @Test
    fun `Email, when value is valid address`() {
        val email = Email("test@test.org")
        assertThat(email.value).isEqualTo("test@test.org")
    }

    @Test
    fun `Email, when value is invalid address, then exception is thrown`() {
        assertThatThrownBy { Email("test") }
            .isInstanceOf(AssertionError::class.java)
    }

    @Test
    fun `Email, when value empty or black string, then exception is thrown`() {
        assertThatThrownBy { Email(" ") }
            .isInstanceOf(AssertionError::class.java)

        assertThatThrownBy { Email("") }
    }

    @Test
    fun `FirstName, when value is valid`() {
        val firstName = FirstName("John")
        assertThat(firstName.value).isEqualTo("John")
    }

    @Test
    fun `FirstName, when value empty or black string, then exception is thrown`() {
        assertThatThrownBy { FirstName(" ") }
            .isInstanceOf(AssertionError::class.java)

        assertThatThrownBy { FirstName("") }
    }

    @Test
    fun `LastName, when value is valid`() {
        val lastName = LastName("Doe")
        assertThat(lastName.value).isEqualTo("Doe")
    }

    @Test
    fun `LastName, when value empty or black string, then exception is thrown`() {
        assertThatThrownBy { LastName(" ") }
            .isInstanceOf(AssertionError::class.java)

        assertThatThrownBy { LastName("") }
    }

    @Test
    fun `Version, when value is valid`() {
        val version = Version(1)
        assertThat(version.value).isEqualTo(1)
    }

    @Test
    fun `Version, when value is negative, then exception is thrown`() {
        assertThatThrownBy { Version(-1) }
            .isInstanceOf(AssertionError::class.java)
    }

    @Test
    fun `Version, when increment, then value is incremented`() {
        val version = Version(1)
        assertThat(version.increment().value).isEqualTo(2)
    }
}