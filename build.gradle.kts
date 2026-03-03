plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

subprojects {
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        filter {
            exclude { element ->
                val path = element.file.path
                path.contains("/build/") || path.contains("/generated/")
            }
        }
    }

    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>().configureEach {
        exclude("**/generated/**")
        exclude("**/build/**")
    }

    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>().configureEach {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

subprojects {
    afterEvaluate {
        apply(plugin = "dev.detekt")

        configure<dev.detekt.gradle.extensions.DetektExtension> {
            toolVersion = libs.versions.detekt.get()
            config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = false
            ignoreFailures = false
            baseline = file("${project.projectDir}/detekt-baseline.xml")
            source.setFrom(
                files(
                    // KMP source sets
                    "src/commonMain",
                    "src/androidMain",
                    "src/iosMain",
                    "src/jvmMain",
                    "src/jsMain",
                    "src/wasmJsMain",
                    "src/commonTest",
                    "src/androidUnitTest",
                    "src/iosTest",
                    "src/jvmTest",
                    "src/jsTest",
                    "src/wasmJsTest",
                    // Android standard source sets
                    "src/main/kotlin",
                    "src/test/kotlin",
                    "src/androidTest/kotlin",
                ),
            )
        }

        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            jvmTarget = "21"
            reports {
                html.required.set(true)
                html.outputLocation.set(file("build/reports/detekt.html"))
                sarif.required.set(true)
                sarif.outputLocation.set(file("build/reports/detekt.sarif"))
                checkstyle.required.set(true)
                checkstyle.outputLocation.set(file("build/reports/detekt.xml"))
            }
        }
    }
}
