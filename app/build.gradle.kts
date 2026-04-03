// Build script del modulo applicativo :app.
// Qui si configurano SDK target, dipendenze concrete e opzioni di compilazione.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Plugin del compilatore Compose (obbligatorio con Kotlin 2.x)
    alias(libs.plugins.kotlin.compose)
    // Genera il codice per kotlinx.serialization (@Serializable)
    alias(libs.plugins.kotlin.serialization)
    // Hilt richiede KSP per la code generation (più veloce di kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.dosagecalc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dosagecalc"
        minSdk = 26          // API 26 = Android 8.0: copertura >95% dei dispositivi attivi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // ProGuard/R8 ottimizza e oscura il codice in produzione.
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // In debug la minificazione è disabilitata per velocizzare le build.
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Abilita i report del compilatore Compose per debugging del recomposition
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    buildFeatures {
        compose = true
        // Disabilitiamo buildConfig se non servono variabili di build runtime
        buildConfig = false
    }

    // Cartella degli asset: qui andrà il file JSON dei farmaci
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
        }
    }

    // Configurazione per JUnit 5 negli unit test
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    // --- AndroidX Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose (BOM gestisce le versioni compatibili automaticamente) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- Hilt - Dependency Injection ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)                     // KSP genera il codice Hilt a compile-time
    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)

    // --- Serializzazione JSON (per leggere assets/drugs.json) ---
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.room.paging)
    
    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // --- WorkManager & Hilt Work ---
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // --- Coroutine (per operazioni asincrone: lettura file, calcoli su IO dispatcher) ---
    implementation(libs.kotlinx.coroutines.android)

    // --- Debug Tools (solo nelle build di debug) ---
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ===================== TESTING =====================

    // Unit Test (JVM) - JUnit 5 + MockK + Turbine (per Flow)
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.params)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.mockk.core)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented Test (Android Device/Emulator)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
