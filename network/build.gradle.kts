@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("kotlinx-serialization")
    id("com.squareup.wire")
    id("kotlin-parcelize")
}

val buildIosTarget = project.findProperty("target.ios") == true
val buildWasmTarget = project.findProperty("target.wasm") == true

kotlin {
    if (buildWasmTarget) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            moduleName = "composeApp"
            browser {
                commonWebpackConfig {
                    outputFileName = "composeApp.js"
                }
            }
            binaries.executable()
        }
    }


    androidTarget {
        compilations.all {
            @Suppress("DEPRECATION")
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    jvm()

    if (buildIosTarget) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }



    sourceSets {
        androidMain.dependencies {
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
    jvmToolchain(11)
}

android {
    namespace = "com.storyteller_f.bi.network"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    testOptions {
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    with("de.jensklingenberg.ktorfit:ktorfit-ksp:${libs.versions.ktorfitVersion.get()}") {
        add("kspAndroid", this)
        if (buildIosTarget) {
            add("kspIosX64", this)
            add("kspIosArm64", this)
            add("kspIosSimulatorArm64", this)
        }

    }
}

wire {
    sourcePath {
        srcDir("src/commonMain/protos")
    }
    kotlin {
        includes = listOf("bilibili.*")
        rpcRole = "client"
    }
}
