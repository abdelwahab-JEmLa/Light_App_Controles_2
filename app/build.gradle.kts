import java.util.Properties

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

//noinspection UseTomlInstead
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.light_app_controles"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.light_app_controles"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.2.appTestWhatspp"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        vectorDrawables { useSupportLibrary = true }
        multiDexEnabled = true

        buildConfigField("String", "DROPBOX_APP_KEY",       "\"${localProps.getProperty("DROPBOX_APP_KEY", "")}\"")
        buildConfigField("String", "DROPBOX_APP_SECRET",    "\"${localProps.getProperty("DROPBOX_APP_SECRET", "")}\"")
        buildConfigField("String", "DROPBOX_REFRESH_TOKEN", "\"${localProps.getProperty("DROPBOX_REFRESH_TOKEN", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-opt-in=androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.kotlin_module"
        }
    }
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res",
                "src/main/res-layouts",
                "src/main/res-main",
                "src/main/res-xml"
            )
        }
    }
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
        force("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    }
}

//noinspection UseTomlInstead
dependencies {
    // ─── Core Android ────────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    // ─── Compose BOM ─────────────────────────────────────────────────────────
    implementation("androidx.compose.ui:ui:1.12.0-alpha01")
    implementation("androidx.compose.foundation:foundation:1.12.0-alpha01")
    implementation("androidx.compose.foundation:foundation-layout:1.12.0-alpha01")
    implementation("androidx.compose.ui:ui-graphics:1.12.0-alpha01")
    implementation("androidx.compose.ui:ui-tooling-preview:1.12.0-alpha01")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.4")
    // ─── Material Components (for XML drawables) ─────────────────────────────
    implementation("com.google.android.material:material:1.12.0")

    // ─── Firebase ────────────────────────────────────────────────────────────
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")

    // ─── Room ────────────────────────────────────────────────────────────────
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")


    // ─── Dropbox SDK ─────────────────────────────────────────────────────────
  //  implementation("com.dropbox.core:dropbox-core-sdk:7.0.0")

    // ─── Coroutines + Firebase ───────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ─── Desugaring ──────────────────────────────────────────────────────────
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // ─── Test Dependencies (Instrumented Tests) ───────────────────────────────
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ─── Local Unit Tests ─────────────────────────────────────────────────────
    testImplementation("junit:junit:4.13.2")

    // Calendar
    implementation("com.aminography:primecalendar:1.7.0")

}
