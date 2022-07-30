description = "Common persistence for the the event sourcing"

val scalaBinary = "2.13"

dependencies {
    api(platform("com.typesafe.akka:akka-bom_${scalaBinary}:2.6.19"))
    api("com.typesafe.akka:akka-persistence-typed_${scalaBinary}")
    api("com.typesafe.akka:akka-persistence-query_${scalaBinary}")
    api("org.springframework.data:spring-data-redis")


    compileOnly(group = "io.projectreactor", name = "reactor-core")
    compileOnly(group = "org.springframework", name = "spring-context")
    compileOnly(group = "org.springframework", name = "spring-core")
    compileOnly(group = "org.springframework", name = "spring-webflux")

    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_${scalaBinary}")
    testImplementation("com.typesafe.akka:akka-persistence-testkit_${scalaBinary}")
    testImplementation(libs.assertj.core)
}