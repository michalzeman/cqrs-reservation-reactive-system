import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

subprojects {

    apply {
        plugin("org.springframework.boot")
        plugin("kotlin")
        plugin("java-library")
        plugin("io.spring.dependency-management")
    }

    group = "com.mz.reservation"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17

    extra["springCloudVersion"] = "2021.0.3"

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
//		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//		implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
//		implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

//		implementation("org.springframework.boot:spring-boot-starter-webflux")
//		implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
//		implementation("org.apache.kafka:kafka-streams")
//		implementation("org.springframework.cloud:spring-cloud-stream")
//		implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
//		testImplementation("org.springframework.boot:spring-boot-starter-test")
//		testImplementation("io.projectreactor:reactor-test")

        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project(":common-components") {
    dependencies {
        implementation(project(":common-api"))
    }
}

project("common-eventsourcing-persistence") {
    dependencies {
        implementation(project(":common-api"))
    }
}
