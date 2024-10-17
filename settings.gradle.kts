@file:Suppress("UnstableApiUsage")

rootProject.name = "Bi2"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.universablockchain.com/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental") // for ktor 3.0.0-wasm2
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenLocal()
    }
}

include(":composeApp")
include("network")
