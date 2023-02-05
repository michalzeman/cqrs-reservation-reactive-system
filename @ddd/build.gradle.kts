description = "DDD basic components"

project(":@ddd:domain-persistence") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:lock-storage-adapter-api"))
    }
}

project(":@ddd:lock-storage-adapter-in-memory") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:lock-storage-adapter-api"))
    }
}