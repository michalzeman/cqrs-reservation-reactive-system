package com.mz.common.testing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TestingApplication

fun main(args: Array<String>) {
    runApplication<TestingApplication>(*args)
}