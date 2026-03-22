plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

application {
    mainClass.set("com.arekalov.tpolab2.MainKt")
}

group = "com.arekalov.tpolab2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*\$\$inlined\$*",
                    "*\$lambda\$*",
                )
                annotatedBy(
                    "*Generated*",
                )
            }
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/detekt.yml")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html {
            required.set(true)
            outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.html"))
        }
        txt {
            required.set(true)
            outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.txt"))
        }
        xml.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }

    jvmTarget = "17"
}

tasks.register("reports") {
    group = "reporting"
    description = "Открывает отчёты в браузере"

    dependsOn(tasks.test, tasks.named("koverHtmlReport"))

    doLast {
        exec { commandLine("open", "build/reports/tests/test/index.html") }
        exec { commandLine("open", "build/reports/kover/html/index.html") }
    }
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}