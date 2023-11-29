description = "Customer service"

val keySpace = "customer_key_space"

project(":@customer:customer-application") {
    dependencies {
        implementation(project(":@customer:customer-api"))
        implementation(project(":@customer:customer-domain-api"))
        implementation(project(":@customer:customer-domain"))
        implementation(project(":@customer:customer-adapter-rest"))

        implementation(project(":@ddd:domain-persistence"))
        implementation(project(":@ddd:lock-storage-adapter-redis"))
        implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
        implementation(project(":@ddd:event-storage-ser-des-adapter-json"))
    }
}

project(":@customer:customer-adapter-rest") {
    dependencies {
        implementation(project(":@customer:customer-api"))
        implementation(project(":@customer:customer-domain-api"))
        implementation(project(":@customer:customer-domain"))
        implementation(project(":common-components"))
    }
}

project(":@customer:customer-domain-api") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
    }
}

project(":@customer:customer-domain") {
    dependencies {
        implementation(project(":@customer:customer-api"))
        implementation(project(":@customer:customer-domain-api"))

        implementation(project(":@ddd:domain-persistence"))
    }
}

dependencies {
    implementation(project(":@ddd:event-storage-adapter-cassandra-db"))
}

tasks.register<Copy>("processLiquibase") {
    val destDir = "$buildDir/cassandra-db"

    val buildDbDir = file(destDir)
    if (buildDbDir.exists()) buildDbDir.deleteRecursively()

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

    include("**/changelog.xml")
    filesMatching("**/changelog.xml") {
        filter { line ->
            line.replace("001_init-event-storage-db-model", "001_init-customer-event-storage-db-model")
        }
    }
    into(destDir)
    rename("changelog.xml", "event-sourcing-changelog.xml")
    rename("001_init-event-storage-db-model.cql", "001_init-customer-event-storage-db-model.cql")
}
