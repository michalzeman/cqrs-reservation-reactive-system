description = "Common persistence for the the event sourcing"

dependencies {
    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
    compileOnly(group = "org.springframework", name = "spring-webflux")

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)

    testImplementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}