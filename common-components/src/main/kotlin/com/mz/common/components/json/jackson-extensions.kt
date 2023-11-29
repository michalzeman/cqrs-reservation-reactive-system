package com.mz.common.components.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun ObjectMapper.registerKotlinModule(): ObjectMapper {
    return this.registerModule(KotlinModule.Builder().build())
}

fun ObjectMapper.registerRequiredModules(): ObjectMapper {
    return this.registerKotlinModule().registerModules(JavaTimeModule())
}