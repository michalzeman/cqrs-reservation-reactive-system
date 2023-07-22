val springframeworkBootVersion: String by settings
val kotlinVersion: String by settings
val springDependencyManagementVersion: String by settings


pluginManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        gradlePluginPortal()
    }
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
            library("mockito-core", "org.mockito:mockito-core:4.0.0")
            library("mockito-kotlin", "org.mockito.kotlin:mockito-kotlin:4.0.0")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
        }
    }
}

rootProject.name = "cqrs-reservation-reactive-system"
include("common-components")
include("persistence-testing")
include("@ddd")
include("@ddd:common-domain-api")
include("@ddd:domain-persistence")
include("@ddd:lock-storage-adapter-api")
include("@ddd:lock-storage-adapter-in-memory")
include("@ddd:lock-storage-adapter-redis")
include("@ddd:event-data-serd-adapter-api")
include("@ddd:event-storage-adapter-api")
include("@customer")
include("@customer:customer-api")
include("@customer:customer-domain-api")
include("@customer:customer-application")
include("@customer:customer-domain")
include("@reservation")
include("@reservation:reservation-application")