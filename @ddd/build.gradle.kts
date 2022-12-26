description = "DDD basic components"

project(":@ddd:domain-persistence") {
    dependencies {
        implementation(project(":@ddd:common-domain-api"))
    }
}