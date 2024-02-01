import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv_kr"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.orot.menuboss_tv_kr"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
    }

    val flavorList = listOf("dev", "prod")
    flavorDimensions += "version"
    productFlavors {
        flavorList.forEach {
            create(it) {
                if (it == "dev") {
                    applicationIdSuffix = DebugConfig.suffixName
                    versionNameSuffix = DebugConfig.versionName
                    manifestPlaceholders["appLabel"] = "@string/app_label_dev"
                } else {
                    applicationIdSuffix = ReleaseConfig.suffixName
                    versionNameSuffix = ReleaseConfig.versionName
                    manifestPlaceholders["appLabel"] = "@string/app_label"
                }
            }
        }
    }

    flavorList.forEach {
        val debug = "${it}Debug"
        sourceSets.create(debug) {
            kotlin.srcDir("build/generated/ksp/$debug/kotlin")
            println("kotlin $kotlin")
        }
        val release = "${it}Release"
        sourceSets.create(release) {
            kotlin.srcDir("build/generated/ksp/$release/kotlin")
            println("kotlin $kotlin")
        }
    }

    val keystoreProperties = Properties()
    val keystorePropertiesFile = rootProject.file("signing/keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        getByName("debug") {
            if (System.getenv()["CI"].toBoolean()) { // CI=true is exported by Codemagic
                storeFile = file(System.getenv()["CM_KEYSTORE_PATH"] as String)
                storePassword = System.getenv()["CM_KEYSTORE_PASSWORD"]
                keyAlias = System.getenv()["CM_KEY_ALIAS"]
                keyPassword = System.getenv()["CM_KEY_PASSWORD"]
            } else {
                keyAlias = keystoreProperties["devKeyAlias"] as String
                keyPassword = keystoreProperties["devKeyPassword"] as String
                storeFile = file(keystoreProperties["devStoreFile"] as String)
                storePassword = keystoreProperties["devStorePassword"] as String
            }
        }

        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isDebuggable = false
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

    Libraries.AndroidX.run {
        implementation(startup)
        implementation(multidex)
    }
    Libraries.Hilt.run {
        implementation(daggerAndroid)
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