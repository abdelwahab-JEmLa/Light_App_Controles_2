import java.util.Properties

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}


//noinspection UseTomlInstead
plugins {
    id("com.android.application") version "8.6.1"
    id("org.jetbrains.kotlin.android") version "1.9.24"
    id("com.google.gms.google-services") version "4.4.2"
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
    id("io.realm.kotlin")
}

android {
    namespace = "com.example.light_app_controles"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.light_app_controles"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.13.6"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "1.8" }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
        force("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
    }
}

//noinspection UseTomlInstead
dependencies {
    // ─── Core Android ────────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    // ─── Compose BOM ─────────────────────────────────────────────────────────
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    implementation("androidx.compose.ui:ui:1.7.4")

    implementation("androidx.compose.foundation:foundation:1.7.4")

    implementation("androidx.compose.foundation:foundation-layout:1.7.4")

    implementation("androidx.compose.ui:ui-graphics:1.7.4")

    implementation("androidx.compose.ui:ui-tooling-preview:1.7.4")

    implementation("androidx.compose.material3:material3:1.3.0")

    implementation("androidx.wear.compose:compose-material:1.4.0")
    implementation("com.google.android.material:material:1.12.0")
    // ─── Lifecycle / Navigation ───────────────────────────────────────────────

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    implementation("androidx.compose.runtime:runtime-livedata:1.7.4")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.8.6")

    // ─── Firebase ────────────────────────────────────────────────────────────
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-database:21.0.0")

    implementation("com.google.android.engage:engage-core:1.5.5")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.1")

    // ─── AI & Serialization ──────────────────────────────────────────────────
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // ─── Image loading (Glide + Coil) ────────────────────────────────────────
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    // ─── Room ────────────────────────────────────────────────────────────────
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // ─── Utilities ───────────────────────────────────────────────────────────
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
    implementation("androidx.compose.material:material-icons-extended:1.7.4")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.paging:paging-runtime:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

    // ─── Maps (OSMDroid) ─────────────────────────────────────────────────────
    implementation("com.google.android.gms:play-services-nearby:19.3.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("org.osmdroid:osmdroid-wms:6.1.16")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.16")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")

    // ─── Animations (Lottie) ─────────────────────────────────────────────────
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // ─── Koin DI ─────────────────────────────────────────────────────────────
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")

    // ─── Realm ───────────────────────────────────────────────────────────────
    implementation("io.realm.kotlin:library-base:1.12.0")
    implementation("io.realm.kotlin:library-sync:1.12.0")

    // ─── Camera ──────────────────────────────────────────────────────────────
    implementation("com.google.guava:guava:32.1.3-android")
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ─── MongoDB (BSON) ──────────────────────────────────────────────────────
    implementation("org.mongodb:bson:4.11.1")

    // ─── PDF (iText) ─────────────────────────────────────────────────────────
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:html2pdf:4.0.5")

    // ─── Coroutines + Firebase ───────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ─── Dropbox SDK ─────────────────────────────────────────────────────────
    implementation("com.dropbox.core:dropbox-core-sdk:7.0.0")

    // ─── ExoPlayer ───────────────────────────────────────────────────────────
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")

    // ─── ExifInterface ───────────────────────────────────────────────────────
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // ─── Desugaring ──────────────────────────────────────────────────────────
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // ─── Excel (Apache POI) ──────────────────────────────────────────────────
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // ─── Calendar ────────────────────────────────────────────────────────────
    implementation("com.aminography:primecalendar:1.7.0")

    // ─── Tests unitaires ─────────────────────────────────────────────────────
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("io.mockk:mockk-android:1.13.5")
    testImplementation("io.insert-koin:koin-test:3.5.0")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.0")

    // ─── Tests instrumentés ──────────────────────────────────────────────────
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.4")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("io.insert-koin:koin-test:3.5.0")
    androidTestImplementation("io.insert-koin:koin-test-junit4:3.5.0")

    // ─── Debug ───────────────────────────────────────────────────────────────
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.4")
}
