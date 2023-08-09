import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.orot.menuboss_tv"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
    }

    sourceSets {
        create("prod") {
            res.srcDir("src/prod/assets")
        }
        create("dev") {
            res.srcDir("src/dev/assets")
        }
    }

    flavorDimensions += "default"
    productFlavors {
        create("dev") {
            applicationIdSuffix = DebugConfig.suffixName
            versionNameSuffix = DebugConfig.versionName
            manifestPlaceholders["appLabel"] = DebugConfig.app_label
        }
        create("prod") {
            applicationIdSuffix = ReleaseConfig.suffixName
            versionNameSuffix = ReleaseConfig.versionName
            manifestPlaceholders["appLabel"] = ReleaseConfig.app_label
        }
    }

    val keystoreProperties = Properties()
    val keystorePropertiesFile = rootProject.file("signing/keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties["devKeyAlias"] as String
            keyPassword = keystoreProperties["devKeyPassword"] as String
            storeFile = file(keystoreProperties["devStoreFile"] as String)
            storePassword = keystoreProperties["devStorePassword"] as String
        }

        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))

    Libraries.apply {
        Libraries.AndroidX.run {
            implementation(startup)
            implementation(multidex)
        }
        Libraries.Hilt.run {
            implementation(daggerAndroid)
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