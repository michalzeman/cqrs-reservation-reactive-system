description = "DDD basic components"

project(":@ddd:common-domain-api")

project(":@ddd:domain-persistence") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:lock-storage-adapter-api"))
        implementation(project(":@ddd:event-storage-adapter-api"))
        implementation(project(":@ddd:event-data-serd-adapter-api"))
    }
}

project(":@ddd:lock-storage-adapter-in-memory") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:lock-storage-adapter-api"))
    }
}

project(":@ddd:lock-storage-adapter-redis") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:lock-storage-adapter-api"))
    }
}

project(":@ddd:event-data-serd-adapter-api") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:event-storage-adapter-api"))
    }
}

project(":@ddd:event-storage-adapter-api") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
    }
}

project(":@ddd:event-storage-adapter-cassandra-db") {
    dependencies {
        implementation(project(":@ddd:event-storage-adapter-api"))
    }
}