description = "ai-agent service"

val keySpace = "ai_agent_keyspace"

project(":ai-agent:ai-agent-boot-app") {
    dependencies {
        implementation(project(":common-components"))
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":ai-agent:ai-agent-adapter-llm"))
        implementation(project(":ai-agent:ai-agent-adapter-rest"))
        implementation(project(":ai-agent:ai-agent-adapter-customer"))
        implementation(project(":ai-agent:ai-agent-adapter-reservation"))

        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:lock-storage-adapter-redis"))
        implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
        implementation(project(":@ddd:event-storage-ser-des-adapter-json"))
        implementation(project(":@ddd:domain-view"))
        implementation(project(":@ddd:domain-view-adapter-cassandra-db"))
    }
}

project(":ai-agent:ai-agent-domain") {
    dependencies {
        api(project(":ai-agent:ai-agent-domain-api"))
        implementation(project(":common-components"))
        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:lock-storage-adapter-redis"))
    }
}

project(":ai-agent:ai-agent-domain-api") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
        api(project(":reservation:reservation-domain-api"))
        api(project(":customer:customer-domain-api"))
    }
}

project(":ai-agent:ai-agent-adapter-llm") {
    dependencies {
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":customer:customer-adapter-rest-api"))
        implementation(project(":common-components"))
    }
}

project(":ai-agent:ai-agent-adapter-rest") {
    dependencies {
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":common-components"))
    }
}

project(":ai-agent:ai-agent-adapter-customer") {
    dependencies {
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":common-components"))
        implementation(project(":customer:customer-adapter-rest-api"))
        implementation(project(":customer:customer-domain-api"))
    }
}

project(":ai-agent:ai-agent-adapter-reservation") {
    dependencies {
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":common-components"))
        implementation(project(":reservation:reservation-adapter-rest-api"))
    }
}

dependencies {
    implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
    implementation(project(":@ddd:domain-view-adapter-cassandra-db"))
}

tasks.register<Copy>("processLiquibase") {
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"

    val buildDbDir = file(destDir)
    if (buildDbDir.exists()) buildDbDir.deleteRecursively()

    dependsOn("extractedEventSourceChangelog")
    dependsOn("extractedDomainQueryChangelog")
    dependsOn("processAiAgentLiquibase")
}

tasks.register<Copy>("processAiAgentLiquibase") {
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
    include("**/ai-agent-changelog.xml")
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
            line.replace("001_init-event-storage-db-model", "001_init-ai-agent-event-storage-db-model")
        }
    }
    into(destDir)
    rename("001_init-event-storage-db-model.cql", "001_init-ai-agent-event-storage-db-model.cql")
}

tasks.register<Copy>("extractedDomainQueryChangelog") {
    val destDir = "${layout.buildDirectory.get().asFile}/cassandra-db"

    dependsOn(":@ddd:domain-view-adapter-cassandra-db:jar")

    configurations["runtimeClasspath"].resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
        val artifactFile = artifact.file
        if (artifactFile.name.startsWith("domain-view-adapter-cassandra-db") && artifactFile.extension == "jar") {
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
            line.replace("001_init-persistence-view-db-model", "001_init-ai-agent-persistence-view-db-model")
        }
    }
    into(destDir)
    rename("001_init-persistence-view-db-model.cql", "001_init-ai-agent-persistence-view-db-model.cql")
}