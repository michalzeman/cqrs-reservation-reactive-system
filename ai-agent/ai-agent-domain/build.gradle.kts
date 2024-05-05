description = "ai-agent-domain"

dependencies {
    api(libs.langchain4j.core)
    api(libs.langchain4j.local.ai)
    api(libs.langchain4j)

    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
}