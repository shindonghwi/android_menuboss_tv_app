plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.orot.menuboss_tv.core"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
    }
}

dependencies {
    api (files("libs/A3LMessaging-1.1.0.aar"))
}