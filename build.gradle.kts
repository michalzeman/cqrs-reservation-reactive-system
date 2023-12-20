import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinxSerializationJsonVersion: String by project
val springframeworkBootVersion: String by project
val springCloudVersion: String by project
val mockitoCoreVersion: String by project
val kotlinxDatetimeVersion: String by project
val reactorKotlinExtensionsVersion: String by project

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

val jvmTargetVersion = "17"

subprojects {

    apply {
        plugin("java")
        plugin("kotlin")
        plugin("org.jetbrains.kotlin.plugin.serialization")
        plugin("java-library")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.spring")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(jvmTargetVersion))
        }
    }

    group = "com.mz.reservation"
    version = "0.0.1-SNAPSHOT"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springframeworkBootVersion")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("org.jetbrains.kotlin:kotlin-serialization")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorKotlinExtensionsVersion")


        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-junit-jupiter")
        testImplementation("org.mockito:mockito-core:$mockitoCoreVersion")
        testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoCoreVersion")


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
    val allProcessLiquibaseTasks = project.subprojects
        .flatMap { project -> project.tasks.matching { it.name == "processLiquibase" } }
    dependsOn(allProcessLiquibaseTasks)
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
    val allTestTasks = project.subprojects.flatMap { project -> project.tasks.matching { it.name == "test" } }
    mustRunAfter(allTestTasks)
    doLast {
        exec {
            commandLine("docker-compose", "down", "-v")
        }
    }
}

tasks["build"].finalizedBy("tearDownDockerCompose")