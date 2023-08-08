object Versions {

    object Plugins {
        const val gradle = "8.1.0"
        const val kotlin = "1.9.0"
        const val googleService = "4.3.15"
        const val hilt = "2.44"
    }

    object Kotlin {
        const val version = "1.9.0"
    }

    object AndroidX {
        const val startup = "1.1.1"
    }

    object KTX {
        const val core = "1.10.1"
    }

    object Compose {
        const val compiler = "1.5.1"
        const val activity = "1.7.2"
        const val tv = "1.0.0-alpha07"
        const val coil = "2.2.2"
        const val navigation = "2.6.0"
        const val viewModel = "2.6.1"
        const val bom = "2023.01.00"
        const val constraintLayout = "1.0.1"
    }

    object Rive {
        const val android = "5.0.0"
    }

    //
    object Hilt{
        const val compose_hilt = "1.0.0"
        const val version = "2.44"
    }
//
//    object OkHttp{
//        const val version = "4.9.3"
//        const val logging = "3.9.1"
//    }
//
//    object Retrofit{
//        const val version = "2.6.0"
//    }
//
//    object Gson{
//        const val version = "2.10"
//    }
//
//    object Firebase{
//        const val bom = "31.1.0"
//        const val messagingService = "23.1.1"
//    }
//
    object Google {
        const val guava = "28.0-android"
        const val zxing = "3.5.1"
        const val fcm = "23.2.0"
    }
}

object Libraries {

    object AndroidX {
        const val startup = "androidx.startup:startup-runtime:${Versions.AndroidX.startup}"
    }

    object KTX {
        const val core = "androidx.core:core-ktx:${Versions.KTX.core}"
    }

    object Rive {
        const val rive = "app.rive:rive-android:${Versions.Rive.android}"
    }

    object Compose {
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val activity = "androidx.activity:activity-compose:${Versions.Compose.activity}"
        const val tvFoundation = "androidx.tv:tv-foundation:${Versions.Compose.tv}"
        const val tvMaterial = "androidx.tv:tv-material:${Versions.Compose.tv}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.Compose.navigation}"
        const val coil = "io.coil-kt:coil-compose:${Versions.Compose.coil}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.Compose.viewModel}"
        const val bom = "androidx.compose:compose-bom:${Versions.Compose.bom}"
        const val contraintLayout = "androidx.constraintlayout:constraintlayout-compose:${Versions.Compose.constraintLayout}"
    }


    object Google {
        const val guava = "com.google.guava:guava:${Versions.Google.guava}"
        const val zxing = "com.google.zxing:core:${Versions.Google.zxing}"
        const val fcm = "com.google.firebase:firebase-messaging:${Versions.Google.fcm}"
    }

    object Hilt {
        const val NavigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.Hilt.compose_hilt}"
        const val dagger = "com.google.dagger:hilt-android:${Versions.Hilt.version}"
        const val core = "com.google.dagger:hilt-core:${Versions.Hilt.version}"
    }

//    object OkHttp{
//        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp.version}"
//        const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.OkHttp.version}"
//    }
//
//    object Retrofit{
//        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.Retrofit.version}"
//        const val retrofit_gson = "com.squareup.retrofit2:converter-gson:${Versions.Retrofit.version}"
//    }
//
//    object Gson{
//        const val gson = "com.google.code.gson:gson:${Versions.Gson.version}"
//    }
//
//    object Firebase{
//        const val bom = "com.google.firebase:firebase-bom:${Versions.Firebase.bom}"
//        const val analytics = "com.google.firebase:firebase-analytics"
//        const val messaging = "com.google.firebase:firebase-messaging:${Versions.Firebase.messagingService}"
//    }
}

object Kapts {
    object Hilt{
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.Hilt.version}"
    }
}
