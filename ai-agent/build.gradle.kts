description = "ai-agent service"


project(":ai-agent:ai-agent-application") {
    dependencies {
        implementation(project(":common-components"))
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":ai-agent:ai-agent-adapter-llm"))
        implementation(project(":ai-agent:ai-agent-adapter-rest"))
    }
}

project(":ai-agent:ai-agent-domain") {
    dependencies {
        implementation(project(":common-components"))
    }
}

project(":ai-agent:ai-agent-adapter-llm") {
    dependencies {
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":common-components"))
    }
}

project(":ai-agent:ai-agent-adapter-rest") {
    dependencies {
        implementation(project(":ai-agent:ai-agent-domain"))
        implementation(project(":common-components"))
    }
}