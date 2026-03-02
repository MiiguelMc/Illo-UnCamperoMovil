plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.illouncampero.illouncampero"
    // Baja el compileSdk al 34 o 35 (el 36 es experimental y da problemas)
    compileSdk = 36

    defaultConfig {
        applicationId = "com.illouncampero.illouncampero"

        // ESTA ES LA LÍNEA CLAVE:
        // Cámbialo a 24 o 26. Esto hará que funcione en el 99% de los móviles.
        minSdk = 26

        // El target debe coincidir con el compileSdk
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")      // Para el Login
    implementation("com.google.firebase:firebase-firestore") // Para los Camperos
    implementation("com.google.firebase:firebase-storage")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.navigation:navigation-compose:2.8.5")

    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Las herramientas para hablar con Spring Boot
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Esta es la que trae los iconos
    implementation("androidx.compose.material:material-icons-extended")

    // Y asegúrate de tener la de Material 3 que es la que estamos usando
    implementation("androidx.compose.material3:material3")

}