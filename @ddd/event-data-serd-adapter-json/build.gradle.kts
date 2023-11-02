dependencies {
    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-data-cassandra-reactive")
}