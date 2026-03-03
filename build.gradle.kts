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
            exclude("**/generated/**")
            exclude("**/build/**")
        }
    }
}

subprojects {
    afterEvaluate {
        apply(plugin = "dev.detekt")

        configure<dev.detekt.gradle.extensions.DetektExtension> {
            toolVersion = "2.0.0-alpha.1"
            config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = false
            ignoreFailures = false
            baseline = file("${project.projectDir}/detekt-baseline.xml")
            source.setFrom(
                files(
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
                ),
            )
        }

        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            jvmTarget = "11"
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
