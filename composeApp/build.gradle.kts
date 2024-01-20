import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.wire)
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

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.storyteller-f.bi.shared")
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.exoplayer.dash)
            implementation(libs.androidx.media3.ui)
            implementation(libs.logback.android)
        }
        commonMain.dependencies {
            //compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            //jetbrains
            implementation(libs.kotlinx.datetime)

            //protos
            implementation(libs.wire.runtime)
            implementation(libs.wire.grpc.client)

            //ktor
            implementation(libs.ktorfit.lib)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            //开源ui组件
            implementation(libs.qrose)
            implementation(libs.image.loader)
            //开源非ui组件
            implementation(libs.md)
            implementation(libs.uuid)
            implementation(libs.uri.kmp)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.mp.stools)
            implementation(libs.material3.window.size.multiplatform)
            implementation(libs.insetsx)
            //框架组件
            implementation(libs.precompose)
            implementation(libs.precompose.viewmodel)
            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)
            implementation(libs.napier)
            implementation(libs.kotlinx.serialization.json)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.preview)
            implementation(libs.qrcode.kotlin.jvm)
            implementation(libs.appdirs)
            implementation(libs.vlcj)
            implementation(libs.logback.classic)
        }
    }
    jvmToolchain(11)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.storyteller.bi2.resources"
    generateResClass = always
}

android {
    namespace = "com.storyteller_f.bi"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.storyteller_f.bi"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.storyteller_f.bi"
            packageVersion = "1.0.0"
        }
    }
}

//compose.experimental {
//    web.application {}
//}

dependencies {
    with("de.jensklingenberg.ktorfit:ktorfit-ksp:${libs.versions.ktorfitVersion.get()}") {
//        add("kspCommonMainMetadata", this)
        add("kspDesktop", this)
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

//https://github.com/JetBrains/compose-multiplatform/issues/4085
//tasks.withType<AndroidLintAnalysisTask> {
//    dependsOn("copyFontsToAndroidAssets")
//}
//
//tasks.withType<LintModelWriterTask> {
//    dependsOn("copyFontsToAndroidAssets")
//}