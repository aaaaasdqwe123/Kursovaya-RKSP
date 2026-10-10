plugins { kotlin("jvm"); kotlin("plugin.serialization"); application }
kotlin { jvmToolchain(21); compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
java { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
application { mainClass.set("ru.pay.server.MainKt") }
val ktor = "2.3.13"
dependencies {
    implementation(project(":contracts")); implementation(project(":domain"))
    listOf("core", "netty", "auth", "auth-jwt", "content-negotiation", "status-pages", "rate-limit").forEach {
        implementation("io.ktor:ktor-server-$it-jvm:$ktor")
    }
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-core:10.20.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.20.1")
    implementation("ch.qos.logback:logback-classic:1.5.12")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor")
    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor")
    testImplementation("org.testcontainers:postgresql:1.20.3")
    testImplementation("org.testcontainers:junit-jupiter:1.20.3")
}
tasks.test { useJUnitPlatform() }
