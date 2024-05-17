import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val kotlinxSerializationJsonVersion: String by project
val springframeworkBootVersion: String by project
val springCloudVersion: String by project
val mockitoCoreVersion: String by project
val kotlinxDatetimeVersion: String by project
val reactorKotlinExtensionsVersion: String by project
val kotlinVersion: String by project

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
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven") }
    }
}

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    group = "com.mz"
    version = "0.0.1-SNAPSHOT"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springframeworkBootVersion")
            mavenBom("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("org.jetbrains.kotlin:kotlin-serialization")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("org.jetbrains.kotlin:kotlin-serialization")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
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

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
            jvmTarget = JvmTarget.JVM_21
        }
    }

    tasks.test {
        useJUnitPlatform {
            excludeTags("systemChecks", "aiTest")
        }
    }

    tasks.register<Test>("systemTest") {
        group = "verification"
        useJUnitPlatform {
            includeTags("systemChecks")
        }
        dependsOn(":systemChecksTests")
    }
}

project(":common-components") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
    }
}

project(":reservation-system-checks-tests") {
    dependencies {
        implementation(project(":common-components"))
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:domain-view"))
        implementation(project(":@ddd:lock-storage-adapter-api"))
        implementation(project(":@ddd:lock-storage-adapter-in-memory"))
        implementation(project(":@ddd:lock-storage-adapter-redis"))
        implementation(project(":@ddd:event-storage-ser-des-adapter-api"))
        implementation(project(":@ddd:event-storage-ser-des-adapter-json"))
        implementation(project(":@ddd:event-storage-adapter-api"))
        implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
        implementation(project(":customer:customer-adapter-rest"))
        implementation(project(":customer:customer-adapter-rest-api"))
        implementation(project(":customer:customer-adapter-kafka"))
        implementation(project(":customer:customer-domain-api"))
        implementation(project(":customer:customer-application"))
        implementation(project(":customer:customer-domain"))
        implementation(project(":reservation:reservation-application"))
        implementation(project(":reservation:reservation-domain-api"))
        implementation(project(":reservation:reservation-domain"))
        implementation(project(":reservation:reservation-adapter-rest"))
        implementation(project(":reservation:reservation-adapter-rest-api"))
        implementation(project(":reservation:reservation-adapter-kafka"))
    }
}

tasks.register("runDockerCompose") {
    group = "docker"

    dependsOn(":processAllLiquibase")
    mustRunAfter(":processAllLiquibase")
    doLast {
        dockerInfrastructureUp()
    }
}

tasks.register("waitForDockerCompose") {
    group = "docker"

    mustRunAfter(":runDockerCompose", ":springBootBuildImagesAndRunDockerContainers")
    doLast {
        waitToDockerInfrastructureIsHealthy()
    }
}

tasks.register("tearDownDockerCompose") {
    group = "docker"

    val allTestTasks = project.subprojects.flatMap { project -> project.tasks.matching { it.name == "systemTest" } }
    mustRunAfter(allTestTasks)
    doLast {
        exec {
            commandLine(
                "sh",
                "-c",
                "docker compose --profile system-checks down -v"
            )
        }
    }
}

tasks.register("processAllLiquibase") {
    mustRunAfter("compileKotlin", "compileJava")
    val allProcessLiquibaseTasks = project.subprojects
        .flatMap { project -> project.tasks.matching { it.name == "processLiquibase" } }
    dependsOn(allProcessLiquibaseTasks)
}

tasks.register("springBootBuildImagesAndRunDockerContainers") {
    group = "docker"

    val allBootBuildImagesTasks = project.subprojects
        .filter { it.name != "api-gateway" && !it.name.contains("ai-agent") }
        .flatMap { project -> project.tasks.matching { it.name == "bootBuildImage" } }
    dependsOn(allBootBuildImagesTasks)
    mustRunAfter(":runDockerCompose")

    doLast {
        dockerInfrastructureUp("system-checks")
    }
}

tasks.register("systemChecksTests") {
    dependsOn(":buildAndRunDockerInfrastructure")
    mustRunAfter(":buildAndRunDockerInfrastructure")
}

tasks.register("buildAndRunDockerInfrastructure") {
    group = "docker"

    dependsOn(":springBootBuildImagesAndRunDockerContainers", ":runDockerCompose", ":waitForDockerCompose")
    mustRunAfter(":springBootBuildImagesAndRunDockerContainers", ":runDockerCompose", ":waitForDockerCompose")
}

fun dockerInfrastructureUp(profile: String? = null) {
    try {
        exec {
            workingDir = projectDir
            if (profile != null) commandLine(
                "sh",
                "-c",
                "docker compose --profile $profile up -d")
            else commandLine(
                "sh",
                "-c",
                "docker compose up -d")
            standardOutput = System.out
            errorOutput = System.err
        }
    } catch (e: Exception) {
        logger.error("Error running Docker Compose: ${e.message}")
        logger.error("Make sure Docker is installed and available in your PATH")
        throw GradleException("Docker Compose execution failed. Is Docker installed and running?", e)
    }
}

fun waitToDockerInfrastructureIsHealthy() {
    val startTime = System.currentTimeMillis()
    val timeout = 60 * 1000 // 60 seconds

    // wait for Docker containers to be healthy
    while (true) {
        try {
            val result = exec {
                workingDir = projectDir
                commandLine(
                    "sh",
                    "-c",
                    "docker inspect --format='{{.Name}}: {{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' $(docker ps -q) | grep -E '(healthy|running)'"
                )
                isIgnoreExitValue = true
                standardOutput = System.out
                errorOutput = System.err
            }
            if (result.exitValue == 0) {
                break
            }
        } catch (e: Exception) {
            logger.error("Error checking Docker container health: ${e.message}")
            throw GradleException("Failed to check Docker container health. Is Docker installed and running?", e)
        }

        if (System.currentTimeMillis() - startTime > timeout) {
            throw GradleException("Docker containers did not reach a healthy state within the timeout")
        }

        Thread.sleep(1000)
    }
}

tasks["build"].dependsOn("processAllLiquibase")

tasks["systemChecksTests"].dependsOn("processAllLiquibase").finalizedBy("tearDownDockerCompose")