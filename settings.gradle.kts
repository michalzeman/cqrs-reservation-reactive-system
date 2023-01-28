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
            library("guava", "com.google.guava:guava:28.1-jre")
            library("projectreactor-blockhound", "io.projectreactor.tools:blockhound:1.0.4.RELEASE")
            library("apache-commons-lang3", "org.apache.commons:commons-lang3:3.9")
            library("assertj-core", "org.assertj:assertj-core:3.14.0")
            library("mockito-core", "org.mockito:mockito-core:4.0.0")
            library("mockito-kotlin", "org.mockito.kotlin:mockito-kotlin:4.0.0")
        }
    }
}

rootProject.name = "cqrs-reservation-reactive-system"
include("common-components")
include("persistence-testing")
include("@ddd")
include("@ddd:common-domain-api")
include("@ddd:domain-persistence")
include("@ddd:lock-storage-in-memory-adapter")
include("@customer")
include("@customer:customer-api")
include("@customer:customer-domain-api")
include("@customer:customer-application")
include("@customer:customer-domain")
include("@reservation")
include("@reservation:reservation-application")
