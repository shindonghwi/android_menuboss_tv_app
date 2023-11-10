plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.orot.menuboss_tv.logging"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    Libraries.Google.run {
        api(platform(firebaseBom))
        api(firebaseCrashlyticsKtx)
        api(firebaseAnallyticsKtx)
        api(playServiceMeasurement)
    }
}