description = "Customer service"

project(":@customer:customer-application") {
    dependencies {
        implementation(project(":@customer:customer-api"))
        implementation(project(":@customer:customer-domain-api"))
    }
}

project(":@customer:customer-domain-api") {
    dependencies {
        api(project(":common-api"))
    }
}

project(":@customer:customer-domain") {
    dependencies {
        implementation(project(":@customer:customer-api"))
        implementation(project(":@customer:customer-domain-api"))
    }
}