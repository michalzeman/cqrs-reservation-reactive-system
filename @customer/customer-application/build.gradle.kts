apply {
    plugin("org.springframework.boot")
//    plugin("org.jetbrains.kotlin.plugin.spring")
}

description = "Customer application"

dependencies {
    implementation(project(":@ddd:domain-persistence"))
    implementation(project(":@ddd:lock-storage-adapter-redis"))
    implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
    implementation(project(":@ddd:event-storage-ser-des-adapter-json"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}