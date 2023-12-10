description = "DDD basic components"

project(":@ddd:common-domain-api")

project(":@ddd:domain-persistence") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
        api(project(":@ddd:lock-storage-adapter-api"))
        api(project(":@ddd:event-storage-adapter-api"))
        api(project(":@ddd:event-storage-ser-des-adapter-api"))

        testImplementation(project(":@ddd:lock-storage-adapter-redis"))
        testImplementation(project(":@ddd:event-storage-adapter-cassandra-db"))
        testImplementation(project(":@ddd:event-storage-ser-des-adapter-json"))
    }
}

project(":@ddd:domain-query") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
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

project(":@ddd:event-storage-ser-des-adapter-api") {
    dependencies {
        api(project(":@ddd:common-domain-api"))
        api(project(":@ddd:event-storage-adapter-api"))
    }
}


project(":@ddd:event-storage-ser-des-adapter-json") {
    dependencies {
        api(project(":@ddd:event-storage-ser-des-adapter-api"))
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