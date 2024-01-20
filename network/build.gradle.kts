@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("kotlinx-serialization")
    id("com.squareup.wire")
    id("kotlin-parcelize")
}

kotlin {
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//            }
//        }
//        binaries.executable()
//    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()


    sourceSets {
        //val desktopMain by getting

        androidMain.dependencies {
        }
        commonMain.dependencies {

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
        }
        //desktopMain.dependencies {
        //}
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
//        add("kspCommonMainMetadata", this)
//        add("kspDesktop", this)
        add("kspAndroid", this)
        add("kspIosX64", this)
        add("kspIosArm64", this)
        add("kspIosSimulatorArm64", this)
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
