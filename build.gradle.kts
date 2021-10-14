import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
//    kotlin("jvm") version "1.5.20" apply false
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

group = "ru.spbu.math-cs"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "pf-2021-viz"
            packageVersion = "1.0.0"
        }
    }
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
var targetArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

subprojects {
    ext {
        set("target", "${targetOs}-${targetArch}")
        var version = "0.0.0-SNAPSHOT"
        if (project.hasProperty("skiko.version")) {
            version = project.properties["skiko.version"] as String
        }
        set ("version", version)
    }
}