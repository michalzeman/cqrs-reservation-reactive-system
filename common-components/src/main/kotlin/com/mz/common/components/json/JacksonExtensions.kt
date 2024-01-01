package com.mz.common.components.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime


fun ObjectMapper.registerKotlinModule(): ObjectMapper {
    return this.registerModule(KotlinModule.Builder().build())
        .registerKotlinxDateTimeModule()
}

fun ObjectMapper.registerRequiredModules(): ObjectMapper {
    return this.registerKotlinModule().registerModules(JavaTimeModule())
}

fun ObjectMapper.registerKotlinxDateTimeModule(): ObjectMapper {
    val module = SimpleModule().apply {
        addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
        addSerializer(Instant::class.java, InstantSerializer())
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
        addDeserializer(Instant::class.java, InstantDeserializer())
    }
    return this.registerModule(module)
}

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        return LocalDateTime.parse(p.valueAsString)
    }
}

class InstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

class InstantDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        return Instant.parse(p.valueAsString)
    }
}