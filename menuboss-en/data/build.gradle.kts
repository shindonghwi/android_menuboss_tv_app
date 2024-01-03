plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv.data"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
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
        sourceCompatibility = AppConfig.javaVersion
        targetCompatibility = AppConfig.javaVersion
    }
    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(mapOf("path" to ":presentation")))

    Libraries.Coroutine.apply {
        implementation(core)
    }

    Libraries.OkHttp.apply {
        implementation(okhttp)
        implementation(logging)
    }

    Libraries.Retrofit.apply {
        implementation(retrofit)
        implementation(retrofit_gson)
    }

    Kapts.Hilt.run {
        kapt(daggerHiltCompiler)
        kapt(daggerHiltAndroidCompiler)
        kapt(daggerHiltAndroid)
    }

    implementation("androidx.datastore:datastore-preferences:1.0.0")

}

kapt {
    correctErrorTypes = true
}