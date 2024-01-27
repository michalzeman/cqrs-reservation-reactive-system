description = "Customer service"

val keySpace = "customer_keyspace"

project(":customer:customer-application") {
    dependencies {
        implementation(project(":customer:customer-domain-api"))
        implementation(project(":customer:customer-domain-api"))
        implementation(project(":customer:customer-domain"))
        implementation(project(":customer:customer-adapter-rest"))
        implementation(project(":customer:customer-adapter-kafka"))
        implementation(project(":common-components"))

        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:lock-storage-adapter-redis"))
        implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
        implementation(project(":@ddd:event-storage-ser-des-adapter-json"))
        implementation(project(":@ddd:domain-query"))
    }
}

project(":customer:customer-adapter-kafka") {
    dependencies {
        implementation(project(":customer:customer-domain-api"))
        implementation(project(":customer:customer-domain"))
        implementation(project(":common-components"))

        implementation(project(":reservation:reservation-domain-api"))
    }
}

project(":customer:customer-adapter-rest") {
    dependencies {
        implementation(project(":customer:customer-domain-api"))
        implementation(project(":customer:customer-domain"))
        implementation(project(":common-components"))
    }
}

project(":customer:customer-domain-api") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
    }
}

project(":customer:customer-domain") {
    dependencies {
        implementation(project(":customer:customer-domain-api"))
        implementation(project(":reservation:reservation-domain-api"))

        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:domain-query"))
        implementation(project(":common-components"))
    }
}

dependencies {
    implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
    implementation(project(":@ddd:domain-query"))
}

tasks.register<Copy>("processLiquibase") {
//    val destDir = "$buildDir/cassandra-db"
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"

    val buildDbDir = file(destDir)
    if (buildDbDir.exists()) buildDbDir.deleteRecursively()

    dependsOn("extractedEventSourceChangelog")
    dependsOn("extractedDomainQueryChangelog")
    dependsOn("processCustomerLiquibase")
}

tasks.register<Copy>("processCustomerLiquibase") {
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
    include("**/customer-changelog.xml")
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
            line.replace("001_init-event-storage-db-model", "001_init-customer-event-storage-db-model")
        }
    }
    into(destDir)
    rename("001_init-event-storage-db-model.cql", "001_init-customer-event-storage-db-model.cql")
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
            line.replace("001_init-persistence-view-db-model", "001_init-customer-persistence-view-db-model")
        }
    }
    into(destDir)
    rename("001_init-persistence-view-db-model.cql", "001_init-customer-persistence-view-db-model.cql")
}
