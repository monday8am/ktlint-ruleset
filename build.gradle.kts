plugins {
    kotlin("jvm") version "1.8.0"
}

group = "de.komoot.ruleset.ktlint.android"
version = "1.0"
val kotlinVersion = "0.49.0"

repositories {
    mavenCentral()
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
