import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springframeworkBootVersion: String by project
val springCloudVersion: String by project

plugins {
    alias(libs.plugins.springframework.boot) apply false
    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
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
		testImplementation("org.springframework.boot:spring-boot-starter-test")
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

tasks.register("runDockerComposeBeforeTests") {
    dependsOn("testClasses")
    doLast {
        exec {
            commandLine("docker-compose", "up", "-d")
        }

        val startTime = System.currentTimeMillis()
        val timeout = 60 * 1000 // 60 seconds

        // wait for Docker containers to be healthy
        while (true) {
            val result = exec {
                commandLine("docker-compose", "ps")
            }
            if (result.exitValue == 0) {
                break
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                throw GradleException("Docker containers did not reach a healthy state within the timeout")
            }

            Thread.sleep(1000)
        }
    }
}

tasks["test"].dependsOn("runDockerComposeBeforeTests")

tasks.register("tearDownDockerCompose") {
    mustRunAfter("test")
    doLast {
        exec {
            commandLine("docker-compose", "down", "-v")
        }
    }
}
