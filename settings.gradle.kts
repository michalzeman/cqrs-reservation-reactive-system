val springframeworkBootVersion: String by settings
val kotlinVersion: String by settings
val springDependencyManagementVersion: String by settings
val kotlinxSerializationJsonVersion: String by settings
val mockitoCoreVersion: String by settings


pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
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
include("customer")
include("customer:customer-adapter-rest")
include("customer:customer-adapter-kafka")
include("customer:customer-api")
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
