plugins {
    kotlin("jvm") apply false
    kotlin("android") apply false
    kotlin("kapt") apply false
    kotlin("plugin.serialization") apply false
    kotlin("plugin.compose") apply false
    id("com.android.application") apply false
    id("com.google.dagger.hilt.android") apply false
}
allprojects { group = "ru.coursework.payments"; version = "1.0.0" }
