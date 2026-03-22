plugins {
    kotlin("jvm")
    application
    id("org.jetbrains.kotlinx.kover")
    id("io.gitlab.arturbosch.detekt")
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
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.3")
    runtimeOnly("org.slf4j:slf4j-nop:2.0.16")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*\$\$inlined\$*",
                    "*\$lambda\$*",
                    "com.arekalov.tpolab2.MainKt",
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
    config.setFrom(layout.projectDirectory.file("detekt.yml"))
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    setSource(files("src/main/kotlin"))
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
    description = "Открывает отчёты в браузере (JUnit и Kover)"

    dependsOn(tasks.test, tasks.named("koverHtmlReport"))

    doLast {
        val testIndex = layout.buildDirectory.file("reports/tests/test/index.html").get().asFile
        if (testIndex.isFile) {
            exec { commandLine("open", testIndex.absolutePath) }
        }
        val koverIndex = layout.buildDirectory.file("reports/kover/html/index.html").get().asFile
        if (koverIndex.isFile) {
            exec { commandLine("open", koverIndex.absolutePath) }
        }
    }
}

tasks.check {
    dependsOn(tasks.detekt)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
