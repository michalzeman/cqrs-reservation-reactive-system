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
            version("kotlin", kotlinVersion)
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("kotlin-plugin.spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")
            plugin("io.spring.dependency-management", "io.spring.dependency-management").version(
                springDependencyManagementVersion
            )
            plugin("springframework-boot", "org.springframework.boot").version(springframeworkBootVersion)
            // libraries ----------------------------->
            library(
                "kotlinx-serialization-json",
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion"
            )
            library("kotlin-stdlib", "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
            library("guava", "com.google.guava:guava:28.1-jre")
            library("projectreactor-blockhound", "io.projectreactor.tools:blockhound:1.0.4.RELEASE")
            library("apache-commons-lang3", "org.apache.commons:commons-lang3:3.9")
            library("assertj-core", "org.assertj:assertj-core:3.14.0")
            library("mockito-core", "org.mockito:mockito-core:$mockitoCoreVersion")
            library("mockito-kotlin", "org.mockito.kotlin:mockito-kotlin:$mockitoCoreVersion")
            library(
                "kotlinx-serialization-json",
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion"
            )
            library("langchain4j-core", "dev.langchain4j:langchain4j-core:$langchain4jVersion")
            library("langchain4j-local-ai", "dev.langchain4j:langchain4j-local-ai:$langchain4jVersion")
            library("langchain4j", "dev.langchain4j:langchain4j:$langchain4jVersion")
        }
    }
}

rootProject.name = "cqrs-reservation-reactive-system"
include("common-components")
include("@ddd")
include("@ddd:common-domain-api")
include("@ddd:domain-persistence")
include("@ddd:domain-view")
include("@ddd:domain-view-adapter-cassandra-db")
include("@ddd:lock-storage-adapter-api")
include("@ddd:lock-storage-adapter-in-memory")
include("@ddd:lock-storage-adapter-redis")
include("@ddd:event-storage-ser-des-adapter-api")
include("@ddd:event-storage-ser-des-adapter-json")
include("@ddd:event-storage-adapter-api")
include("@ddd:event-storage-adapter-cassandra-db")
include("@ddd:shared-kernel-test-cassandra-db")
include("ai-agent")
include("ai-agent:ai-agent-adapter-llm")
include("ai-agent:ai-agent-adapter-rest")
include("ai-agent:ai-agent-application")
include("ai-agent:ai-agent-domain")
include("customer")
include("customer:customer-adapter-rest")
include("customer:customer-adapter-kafka")
include("customer:customer-domain-api")
include("customer:customer-application")
include("customer:customer-domain")
include("reservation")
include("reservation:reservation-application")
include("reservation:reservation-domain-api")
include("reservation:reservation-domain")
include("reservation:reservation-adapter-rest")
include("reservation:reservation-adapter-kafka")
include("reservation-system-checks-tests")
