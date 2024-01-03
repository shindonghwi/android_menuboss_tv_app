plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv_kr.presentation"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    buildFeatures.compose = true

    compileOptions {
        sourceCompatibility = AppConfig.javaVersion
        targetCompatibility = AppConfig.javaVersion
    }
    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.Compose.compiler
    }
}

dependencies {

    api(project(":domain"))

    Libraries.AndroidX.run {
        implementation(core)
        implementation(exoplayer)
    }

    Libraries.Compose.run {
        implementation(uiTooling)
        implementation(activity)
        implementation(tvFoundation)
        implementation(tvMaterial)
        implementation(coil)
        implementation(navigation)
        implementation(viewModel)
        implementation(bom)
        implementation(contraintLayout)
        implementation(material3)
    }

    Libraries.Google.run {
        implementation(guava)
        implementation(zxing)
        implementation(fcm)
    }

    Libraries.Rive.run {
        implementation(rive)
    }

    Libraries.Gson.run {
        implementation(gson)
    }

    Libraries.Hilt.run {
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