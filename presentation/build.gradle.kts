plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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
    api (project(":core"))


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



        Libraries.Rive.run {
            implementation(rive)
        }

    }
}