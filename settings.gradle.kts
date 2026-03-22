pluginManagement {
    plugins {
        kotlin("jvm") version "2.0.21"
        id("org.jetbrains.kotlinx.kover") version "0.8.3"
        id("io.gitlab.arturbosch.detekt") version "1.23.8"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "tpo-lab2"
