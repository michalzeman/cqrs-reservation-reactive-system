description = "DDD basic components"

project(":@ddd:domain-persistence") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
    }
}

project(":@ddd:lock-storage-in-memory-adapter") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
        implementation(project(":@ddd:domain-persistence"))
    }
}