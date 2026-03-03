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
    apply(plugin = "io.gitlab.arturbosch.detekt")
}

subprojects {
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        filter {
            exclude("**/generated/**")
            exclude("**/build/**")
        }
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = "1.23.8"
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        ignoreFailures = false
        baseline = file("${project.projectDir}/detekt-baseline.xml")
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "11"
        reports {
            html.required.set(true)
            html.outputLocation.set(file("build/reports/detekt.html"))
            sarif.required.set(true)
            sarif.outputLocation.set(file("build/reports/detekt.sarif"))
            xml.required.set(true)
            xml.outputLocation.set(file("build/reports/detekt.xml"))
        }
    }

    dependencies {
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    }
}
