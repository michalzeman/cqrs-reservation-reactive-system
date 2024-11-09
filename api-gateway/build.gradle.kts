apply {
    plugin("org.springframework.boot")
}

description = "API gateway"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.1.5")
    implementation("org.springframework.boot:spring-boot-starter")
}