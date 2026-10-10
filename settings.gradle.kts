pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
    plugins {
        kotlin("jvm") version "2.0.21"
        kotlin("android") version "2.0.21"
        kotlin("kapt") version "2.0.21"
        kotlin("plugin.serialization") version "2.0.21"
        kotlin("plugin.compose") version "2.0.21"
        id("com.android.application") version "8.7.3"
        id("com.google.dagger.hilt.android") version "2.52"
    }
}
dependencyResolutionManagement { repositories { google(); mavenCentral() } }
rootProject.name = "ElectronicPaymentSystem"
include(":contracts", ":domain", ":server")
if (!providers.gradleProperty("backendOnly").isPresent) include(":androidApp")
