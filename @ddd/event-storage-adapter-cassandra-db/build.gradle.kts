val keySpace = "ddd_testing_keyspace"

dependencies {
    api(group = "org.springframework.data", name = "spring-data-cassandra")

    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
    compileOnly(group = "org.springframework", name = "spring-webflux")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-data-cassandra-reactive")
}

tasks.register<Copy>("processLiquibase") {
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"
    from("src/main/resources") // replace with your actual directory
    include("**/*.cql")
    into(destDir)

    filesMatching("**/*.cql") {
        filter { line ->
            line.replace("\${key_space}", keySpace)
        }
    }

    from("src/main/resources") // replace with your actual directory
    include("**/event-sourcing-changelog.xml")
    into(destDir)
}

tasks.named("test") {
    dependsOn("processLiquibase")
}