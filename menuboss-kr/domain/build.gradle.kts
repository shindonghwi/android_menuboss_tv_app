plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv_kr.domain"
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
    api(project(":logging"))
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

    Kapts.Hilt.run {
        kapt(daggerHiltCompiler)
        kapt(daggerHiltAndroidCompiler)
        kapt(daggerHiltAndroid)
    }
}

kapt {
    correctErrorTypes = true
}