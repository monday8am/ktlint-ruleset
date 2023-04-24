plugins {
    kotlin("jvm") version "1.8.0"
    `maven-publish`
}

group = "com.github.monday8am"
version = "v0.0.2"
val kotlinVersion = "0.49.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.pinterest.ktlint:ktlint-rule-engine-core:${kotlinVersion}")
    implementation("com.pinterest.ktlint:ktlint-cli-ruleset-core:${kotlinVersion}")
    testImplementation("com.pinterest.ktlint:ktlint-test:${kotlinVersion}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
