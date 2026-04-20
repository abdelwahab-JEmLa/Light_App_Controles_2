// ═══════════════════════════════════════════════════
// ROOT build.gradle.kts  — racine du projet
// Chemin : MonProjet/build.gradle.kts
// ═══════════════════════════════════════════════════
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    //noinspection UseTomlInstead
    dependencies {
        classpath("com.android.tools.build:gradle")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")
    }
}
