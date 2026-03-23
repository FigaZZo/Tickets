plugins {
    application
    id("java")
}

group = "com.example"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val koraVersion = "1.2.9"
val testcontainersVersion = "1.21.3"

dependencies {
    annotationProcessor(platform("ru.tinkoff.kora:kora-parent:$koraVersion"))
    annotationProcessor("ru.tinkoff.kora:annotation-processors")

    implementation(platform("ru.tinkoff.kora:kora-parent:$koraVersion"))
    implementation("ru.tinkoff.kora:config-yaml")
    implementation("ru.tinkoff.kora:http-server-undertow")
    implementation("ru.tinkoff.kora:json-module")
    implementation("ru.tinkoff.kora:database-jdbc")
    implementation("ru.tinkoff.kora:database-flyway")
    implementation("ru.tinkoff.kora:kafka")
    implementation("ru.tinkoff.kora:validation-module")
    implementation("ru.tinkoff.kora:resilient-kora")
    implementation("ru.tinkoff.kora:micrometer-module")
    implementation("ru.tinkoff.kora:scheduling-jdk")
    implementation("ru.tinkoff.kora:openapi-management")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "com.example.reservation.Application"
}

tasks.test {
    useJUnitPlatform()
}
