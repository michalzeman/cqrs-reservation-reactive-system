description = "Reservation service"

val keySpace = "reservation_keyspace"

project(":reservation:reservation-application") {
    dependencies {
        implementation(project(":reservation:reservation-domain-api"))
        implementation(project(":reservation:reservation-domain"))
        implementation(project(":reservation:reservation-adapter-rest"))

        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:lock-storage-adapter-redis"))
        implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
        implementation(project(":@ddd:event-storage-ser-des-adapter-json"))
        implementation(project(":@ddd:domain-query"))
        implementation(project(":common-components"))
    }
}

project(":reservation:reservation-adapter-rest") {
    dependencies {
        implementation(project(":reservation:reservation-domain-api"))
        implementation(project(":reservation:reservation-domain"))
        implementation(project(":common-components"))
    }
}

project(":reservation:reservation-domain-api") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
    }
}

project(":reservation:reservation-domain") {
    dependencies {
        implementation(project(":reservation:reservation-domain-api"))

        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:domain-query"))
    }
}

dependencies {
    implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
    implementation(project(":@ddd:domain-query"))
}

tasks.register<Copy>("processLiquibase") {
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"

    val buildDbDir = file(destDir)
    if (buildDbDir.exists()) buildDbDir.deleteRecursively()

    dependsOn("extractedEventSourceChangelog")
    dependsOn("extractedDomainQueryChangelog")
    dependsOn("processReservationLiquibase")
}

tasks.register<Copy>("processReservationLiquibase") {
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
    include("**/reservation-changelog.xml")
    into(destDir)
}

tasks.register<Copy>("extractedEventSourceChangelog") {
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"

    dependsOn(":@ddd:event-storage-adapter-cassandra-db:jar")

    configurations["runtimeClasspath"].resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
        val artifactFile = artifact.file
        if (artifactFile.name.startsWith("event-storage-adapter-cassandra-db") && artifactFile.extension == "jar") {
            from(zipTree(artifactFile).matching {
                include("**/liquibase/**")
            })
        }
    }

    include("**/*.cql")
    into(destDir)

    filesMatching("**/*.cql") {
        filter { line ->
            line.replace("\${key_space}", keySpace)
        }
    }

    include("**/event-sourcing-changelog.xml")
    filesMatching("**/event-sourcing-changelog.xml") {
        filter { line ->
            line.replace("001_init-event-storage-db-model", "001_init-reservation-event-storage-db-model")
        }
    }
    into(destDir)
    rename("001_init-event-storage-db-model.cql", "001_init-reservation-event-storage-db-model.cql")
}

tasks.register<Copy>("extractedDomainQueryChangelog") {
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"

    dependsOn(":@ddd:domain-query:jar")

    configurations["runtimeClasspath"].resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
        val artifactFile = artifact.file
        if (artifactFile.name.startsWith("domain-query") && artifactFile.extension == "jar") {
            from(zipTree(artifactFile).matching {
                include("**/liquibase/**")
            })
        }
    }

    include("**/*.cql")
    into(destDir)

    filesMatching("**/*.cql") {
        filter { line ->
            line.replace("\${key_space}", keySpace)
        }
    }

    include("**/domain-query-changelog.xml")
    filesMatching("**/domain-query-changelog.xml") {
        filter { line ->
            line.replace("001_init-persistence-view-db-model", "001_init-reservation-persistence-view-db-model")
        }
    }
    into(destDir)
    rename("001_init-persistence-view-db-model.cql", "001_init-reservation-persistence-view-db-model.cql")
}