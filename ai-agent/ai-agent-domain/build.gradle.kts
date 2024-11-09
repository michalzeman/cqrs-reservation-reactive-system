description = "ai-agent-domain"

dependencies {
    implementation(project(":customer:customer-domain-api"))

    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
}