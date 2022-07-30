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
        }
    }
}

rootProject.name = "cqrs-reservation-reactive-system"
include("common-api")
include("common-components")
include("@customer")
include("@customer:customer-api")
findProject(":@customer:customer-api")?.name = "customer-api"
include("@customer:customer-domain-api")
findProject(":@customer:customer-domain-api")?.name = "customer-domain-api"
include("@customer:customer-application")
findProject(":@customer:customer-application")?.name = "customer-application"
include("@customer:customer-domain")
findProject(":@customer:customer-domain")?.name = "customer-domain"
include("@reservation")
include("@reservation:reservation-application")
findProject(":@reservation:reservation-application")?.name = "reservation-application"
include("common-eventsourcing-persistence")
