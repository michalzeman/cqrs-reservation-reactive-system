dependencies {
    api(group = "org.springframework.data", name = "spring-data-cassandra")
    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
    compileOnly(group = "org.springframework", name = "spring-webflux")
    compileOnly("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)

    testImplementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}