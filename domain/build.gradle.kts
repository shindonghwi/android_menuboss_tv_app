plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv.domain"
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
    api(project(":grpc"))

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

    Libraries.Hilt.run {
        api(navigationCompose)
        api(daggerAndroid)
    }

    Libraries.Google.run {
        api(platform(firebaseBom))
        api(firebaseCrashlyticsKtx)
        api(firebaseAnallyticsKtx)
        api(playServiceMeasurement)
    }

    Kapts.Hilt.run {
        kapt(daggerHiltCompiler)
        kapt(daggerHiltAndroidCompiler)
        kapt(daggerHiltAndroid)
    }
}

kapt {
    correctErrorTypes = true
}