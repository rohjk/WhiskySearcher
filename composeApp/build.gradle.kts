import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation("com.google.code.gson:gson:2.10.1")
            implementation("org.jsoup:jsoup:1.16.1")

            api("io.github.qdsfdhvh:image-loader:1.7.8")

            implementation(platform("io.insert-koin:koin-bom:3.5.3"))
            implementation("io.insert-koin:koin-core")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.whiskiki.whisky.searcher"
            packageVersion = "1.0.0"
        }
    }
}
