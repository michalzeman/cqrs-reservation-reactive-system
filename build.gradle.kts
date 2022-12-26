import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springframeworkBootVersion = project.extra["springframeworkBootVersion"]
val springCloudVersion by project.properties

plugins {
    id("org.springframework.boot") version "2.7.1" apply false
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

    val jvmTargetVersion = "17"

    apply {
        plugin("kotlin")
        plugin("java-library")
        plugin("io.spring.dependency-management")
    }

    group = "com.mz.reservation"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_17

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
            mavenBom("org.springframework.boot:spring-boot-dependencies:${springframeworkBootVersion}")
        }
    }

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
        testImplementation("io.projectreactor:reactor-test")

        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.mockito:mockito-junit-jupiter")

    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = jvmTargetVersion
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project(":common-components") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
    }
}
