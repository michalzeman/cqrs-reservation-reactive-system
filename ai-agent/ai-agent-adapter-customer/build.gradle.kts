description = "ai-agent adapter for the customer service"

dependencies {
    implementation(project(":customer:customer-adapter-rest-api"))
    implementation(project(":customer:customer-domain-api"))

    implementation(group = "io.projectreactor", name = "reactor-core")
    implementation(group = "org.springframework", name = "spring-context")
    implementation(group = "org.springframework", name = "spring-core")
    implementation("org.springframework:spring-webflux")
}