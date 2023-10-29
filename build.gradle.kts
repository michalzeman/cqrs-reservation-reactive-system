import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinxSerializationJsonVersion: String by project
val springframeworkBootVersion: String by project
val springCloudVersion: String by project

plugins {
    alias(libs.plugins.springframework.boot) apply false
    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
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
        plugin("org.jetbrains.kotlin.plugin.serialization")
        plugin("java-library")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.spring")
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
        implementation("org.jetbrains.kotlin:kotlin-serialization")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxSerializationJsonVersion}")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

        // https://mvnrepository.com/artifact/io.projectreactor.kotlin/reactor-kotlin-extensions
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")


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
    val allTasks = project.subprojects.flatMap { project -> project.tasks.matching { it.name == "test" } }
    mustRunAfter(allTasks)
    doLast {
        exec {
            commandLine("docker-compose", "down", "-v")
        }
    }
}

tasks["build"].finalizedBy("tearDownDockerCompose")