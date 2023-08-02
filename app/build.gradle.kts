import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
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

    flavorDimensions("default")
    productFlavors {
        create("dev") {
            applicationIdSuffix = DebugConfig.suffixName
            versionNameSuffix = DebugConfig.versionName
            manifestPlaceholders["appLabel"] =  DebugConfig.app_label
        }
        create("prod") {
            applicationIdSuffix = ReleaseConfig.suffixName
            versionNameSuffix = ReleaseConfig.versionName
            manifestPlaceholders["appLabel"] =  ReleaseConfig.app_label
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures.compose = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
}

dependencies {
    val tv_compose_version = "1.0.0-alpha07"
    implementation("androidx.core:core-ktx:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.tv:tv-foundation:$tv_compose_version")
    implementation("androidx.tv:tv-material:$tv_compose_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("androidx.ads:ads-identifier:1.0.0-alpha05")
    implementation("com.google.guava:guava:28.0-android")
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.google.firebase:firebase-messaging:23.1.0")
    implementation(files("libs/A3LMessaging-1.1.0.aar"))
}
