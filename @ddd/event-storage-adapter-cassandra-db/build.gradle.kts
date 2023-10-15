dependencies {
    api(group = "org.springframework.data", name = "spring-data-cassandra")

    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
    compileOnly(group = "org.springframework", name = "spring-webflux")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-data-cassandra-reactive")
}
