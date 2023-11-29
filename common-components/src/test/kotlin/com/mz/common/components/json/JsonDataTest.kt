package com.mz.common.components.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class JsonDataTest {

    val objectMapper: ObjectMapper = jacksonObjectMapper().registerRequiredModules()

    @Test
    fun `deserialize JSON string into the JsonData`() {
        val json = """{"createdAt":"2021-03-01T00:00:00.000Z","key":1213}"""
        val jsonData: JsonData = objectMapper.readValue(json)
        assertThat(jsonData.requiredAtr<Int>("key")).isEqualTo(1213)
        assertThat(
            jsonData.requiredAtr(
                "createdAt",
                Instant::parse
            )
        ).isEqualTo(Instant.parse("2021-03-01T00:00:00.000Z"))
    }

    @Test
    fun `required returns value when key exists and type matches`() {
        val jsonData: JsonData = mapOf("key" to "value")
        val result: String = jsonData.requiredAtr("key")
        assertThat(result).isEqualTo("value")
    }

    @Test
    fun `required throws exception when key does not exist`() {
        val jsonData: JsonData = mapOf("key" to "value")
        assertThatThrownBy { jsonData.requiredAtr<String>("nonexistent") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Missing required attribute nonexistent")
    }

    @Test
    fun `required throws exception when type does not match`() {
        val jsonData: JsonData = mapOf("key" to 123)
        assertThatThrownBy { jsonData.requiredAtr<String>("key") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Attribute key is not of type")
    }

    @Test
    fun `field returns value when key exists and type matches`() {
        val jsonData: JsonData = mapOf("key" to "value")
        val result: String? = jsonData.attribute("key")
        assertThat(result).isEqualTo("value")
    }

    @Test
    fun `field returns null when key does not exist`() {
        val jsonData: JsonData = mapOf("key" to "value")
        val result: String? = jsonData.attribute("nonexistent")
        assertThat(result).isNull()
    }

    @Test
    fun `field returns null when type does not match`() {
        val jsonData: JsonData = mapOf("key" to 123)
        val result: String? = jsonData.attribute("key")
        assertThat(result).isNull()
    }

    @Test
    fun `required returns Instant when key exists and type matches`() {
        val instant = Instant.now()
        val jsonData: JsonData = mapOf("key" to instant)
        val result: Instant = jsonData.requiredAtr("key")
        assertThat(result).isEqualTo(instant)
    }

    @Test
    fun `required throws exception when key exists but type does not match`() {
        val jsonData: JsonData = mapOf("key" to "not an instant")
        assertThatThrownBy { jsonData.requiredAtr<Instant>("key") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Attribute key is not of type")
    }

    @Test
    fun `field returns Instant when key exists and type matches`() {
        val instant = Instant.now()
        val jsonData: JsonData = mapOf("key" to instant)
        val result: Instant? = jsonData.attribute("key")
        assertThat(result).isEqualTo(instant)
    }

    @Test
    fun `field returns null when key exists but type does not match`() {
        val jsonData: JsonData = mapOf("key" to "not an instant")
        val result: Instant? = jsonData.attribute("key")
        assertThat(result).isNull()
    }
}