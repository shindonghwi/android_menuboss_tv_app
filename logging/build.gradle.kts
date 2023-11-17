plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv.DLog"
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
    Libraries.Google.run {
        api(platform(firebaseBom))
        api(firebaseCrashlyticsKtx)
        api(firebaseAnallyticsKtx)
        api(playServiceMeasurement)
    }

    Libraries.Hilt.run {
        api(daggerAndroid)
    }

    Kapts.Hilt.run {
        kapt(daggerHiltCompiler)
        kapt(daggerHiltAndroidCompiler)
        kapt(daggerHiltAndroid)
    }

    api("com.datadoghq:dd-sdk-android-rum:2.2.0")
    api("com.datadoghq:dd-sdk-android-okhttp:2.2.0")


}

kapt {
    correctErrorTypes = true
}