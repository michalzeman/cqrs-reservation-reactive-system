apply {
    plugin("org.springframework.boot")
//    plugin("org.jetbrains.kotlin.plugin.spring")
}

description = "Customer application"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")

    implementation("org.springframework.boot:spring-boot-starter-data-cassandra")
    implementation("org.liquibase:liquibase-core")
    implementation("com.datastax.oss:java-driver-mapper-runtime:4.11.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}