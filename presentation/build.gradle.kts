plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv.presentation"
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

    implementation(project(":domain"))


    Libraries.apply {

        Libraries.KTX.run {
            implementation(core)
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
        }

        Libraries.Google.run {
            implementation(guava)
            implementation(zxing)
            implementation(fcm)
        }

        Libraries.Rive.run {
            implementation(rive)
        }

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