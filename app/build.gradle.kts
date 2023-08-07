import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = AppConfig.packageName
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = AppConfig.packageName
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

    implementation(files("libs/A3LMessaging-1.1.0.aar"))

    implementation("app.rive:rive-android:5.0.0")
    implementation("androidx.startup:startup-runtime:1.1.1")

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
        }

        Libraries.Google.run {
            implementation(guava)
            implementation(zxing)
            implementation(fcm)
        }

    }

//    implementation("androidx.ads:ads-identifier:1.0.0-alpha05")
}
