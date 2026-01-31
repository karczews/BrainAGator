
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidLibrary {
        namespace = "io.github.karczews.brainagator"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()


        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)

        packaging.resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        localDependencySelection {
            selectBuildTypeFrom.set(listOf("debug", "release"))
        }

        androidResources.enable = true

        optimization {
            minify = false
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
    jvmToolchain(21)
}

// For local development and testing adding Compose Preview tool - AGP 9.0.0-beta01 or higher
dependencies {
    "androidRuntimeClasspath"(compose.uiTooling)
}

compose.resources {
    publicResClass = false
    generateResClass = auto
}

compose.desktop {
    application {
        mainClass = "io.github.karczews.brainagator.MainKt"

        nativeDistributions {
            // Macos + Windows + Linux
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.karczews.brainagator"
            packageVersion = "1.0.0"
            
            macOS {
                val entitlementsFile = project.rootProject.file("entitlements.plist")
                if (!entitlementsFile.exists()) {
                    throw GradleException("entitlements.plist not found at ${entitlementsFile.absolutePath}")
                }
                this.entitlementsFile.set(entitlementsFile)
            }
        }
    }
}
