import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.kyrdax.sitehub.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "SiteHub"
            packageVersion = "1.0.0"
            description = "An app to save and quickly open your favorite websites."
            copyright = "© 2026 kyrdax. All rights reserved."
            vendor = "kyrdax"

            windows {
                iconFile.set(project.file("src/main/resources/sitehub_logo.ico"))
                menuGroup = "SiteHub"
                upgradeUuid = "90096b93-dcf9-4ea8-886f-4c5383542322"
            }
        }
    }
}