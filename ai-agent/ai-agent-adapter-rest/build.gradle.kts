dependencies {
    implementation(group = "io.projectreactor", name = "reactor-core")
    implementation(group = "org.springframework", name = "spring-context")
    implementation(group = "org.springframework", name = "spring-core")
    implementation(group = "org.springframework", name = "spring-webflux")

    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}