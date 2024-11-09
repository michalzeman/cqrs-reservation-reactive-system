description = "ai-agent llm adapter"

dependencies {
    api(libs.langchain4j.core)
    api(libs.langchain4j.local.ai)
    api(libs.langchain4j.ollama)
    api(libs.langchain4j.open.ai)
    api(libs.langchain4j)
    api(libs.langchain4j.reactor)

    implementation(group = "io.projectreactor", name = "reactor-core")
    implementation(group = "org.springframework", name = "spring-context")
    implementation(group = "org.springframework", name = "spring-core")
    // spring boot autoconfiguration
    implementation(group = "org.springframework.boot", name = "spring-boot-autoconfigure")
}