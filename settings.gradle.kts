val springframeworkBootVersion: String by settings
val kotlinVersion: String by settings
val springDependencyManagementVersion: String by settings
val kotlinxSerializationJsonVersion: String by settings
val mockitoCoreVersion: String by settings
val langchain4jVersion: String by settings

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        // Adding JetBrains repository to access latest Kotlin 2.1 releases
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Define versions
            version("kotlin", kotlinVersion)
            version("springframeworkBoot", springframeworkBootVersion)
            version("springDependencyManagement", springDependencyManagementVersion)
            version("kotlinxSerializationJson", kotlinxSerializationJsonVersion)
            version("mockitoCore", mockitoCoreVersion)
            version("langchain4j", langchain4jVersion)

            // Define plugins
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("kotlin-plugin.spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")
            plugin("io.spring.dependency-management", "io.spring.dependency-management").versionRef("springDependencyManagement")
            plugin("springframework-boot", "org.springframework.boot").versionRef("springframeworkBoot")

            // Define libraries
            library("kotlin-stdlib", "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
            library("guava", "com.google.guava:guava:28.1-jre")
            library("projectreactor-blockhound", "io.projectreactor.tools:blockhound:1.0.4.RELEASE")
            library("apache-commons-lang3", "org.apache.commons:commons-lang3:3.9")
            library("assertj-core", "org.assertj:assertj-core:3.14.0")
            library("mockito-core", "org.mockito:mockito-core:$mockitoCoreVersion")
            library("mockito-kotlin", "org.mockito.kotlin:mockito-kotlin:$mockitoCoreVersion")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
            library("langchain4j-core", "dev.langchain4j:langchain4j-core:$langchain4jVersion")
            library("langchain4j-local-ai", "dev.langchain4j:langchain4j-local-ai:$langchain4jVersion")
            library("langchain4j-ollama", "dev.langchain4j:langchain4j-ollama:$langchain4jVersion")
            library("langchain4j-open-ai", "dev.langchain4j:langchain4j-open-ai:$langchain4jVersion")
            library("langchain4j-reactor", "dev.langchain4j:langchain4j-reactor:$langchain4jVersion")
            library("langchain4j", "dev.langchain4j:langchain4j:$langchain4jVersion")
        }
    }
}

rootProject.name = "cqrs-reservation-reactive-system"

include(
    "api-gateway",
    "common-components",
    "@ddd",
    "@ddd:common-domain-api",
    "@ddd:domain-persistence",
    "@ddd:domain-view",
    "@ddd:domain-view-adapter-cassandra-db",
    "@ddd:lock-storage-adapter-api",
    "@ddd:lock-storage-adapter-in-memory",
    "@ddd:lock-storage-adapter-redis",
    "@ddd:event-storage-ser-des-adapter-api",
    "@ddd:event-storage-ser-des-adapter-json",
    "@ddd:event-storage-adapter-api",
    "@ddd:event-storage-adapter-cassandra-db",
    "@ddd:shared-kernel-test-cassandra-db",
    "ai-agent",
    "ai-agent:ai-agent-adapter-customer",
    "ai-agent:ai-agent-adapter-reservation",
    "ai-agent:ai-agent-adapter-llm",
    "ai-agent:ai-agent-adapter-rest",
    "ai-agent:ai-agent-application",
    "ai-agent:ai-agent-domain",
    "ai-agent:ai-agent-domain-api",
    "customer-support",
    "customer-support:customer-support-app",
    "customer",
    "customer:customer-adapter-rest",
    "customer:customer-adapter-rest-api",
    "customer:customer-adapter-kafka",
    "customer:customer-domain-api",
    "customer:customer-application",
    "customer:customer-domain",
    "reservation",
    "reservation:reservation-application",
    "reservation:reservation-domain-api",
    "reservation:reservation-domain",
    "reservation:reservation-adapter-rest",
    "reservation:reservation-adapter-rest-api",
    "reservation:reservation-adapter-kafka",
    "reservation-system-checks-tests"
)
