object Versions {

    object Plugins {
        const val gradle = "8.1.0"
        const val kotlin = "1.9.0"
        const val googleService = "4.3.15"
        const val hilt = "2.44"
        const val crashlytics = "2.9.8"
        const val protobuf = "0.9.4"
    }

    object AndroidX {
        const val startup = "1.1.1"
        const val multidex = "2.0.1"
        const val core = "1.10.1"
        const val exoplayer = "2.16.1"
    }

    object Compose {
        const val compiler = "1.5.1"
        const val activity = "1.7.2"
        const val tv = "1.0.0-alpha07"
        const val material3 = "1.1.2"
        const val coil = "2.4.0"
        const val navigation = "2.6.0"
        const val viewModel = "2.6.1"
        const val bom = "2023.01.00"
        const val constraintLayout = "1.0.1"
    }

    object Rive {
        const val android = "5.0.0"
    }

    object Hilt{
        const val compose_hilt = "1.0.0"
        const val version = "2.47"
    }

    object OkHttp{
        const val version = "4.9.3"
        const val logging = "3.9.1"
    }

    object Retrofit{
        const val version = "2.6.0"
    }

    object Gson{
        const val version = "2.10"
    }

    object Coroutine{
        const val version = "1.6.4"
    }

    object Google {
        const val guava = "28.0-android"
        const val zxing = "3.5.1"
        const val fcm = "23.2.0"
        const val firebaseBom = "32.2.2"
        const val playServiceMeasurement = "21.3.0"
    }

    object Protobuf {
        const val grpcVersion = "1.58.0"
        const val grpcKotlinVersion = "1.4.0"
        const val protobufVersion = "3.24.1"
    }

}

object Libraries {

    object AndroidX {
        const val startup = "androidx.startup:startup-runtime:${Versions.AndroidX.startup}"
        const val multidex = "androidx.multidex:multidex:${Versions.AndroidX.multidex}"
        const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        const val exoplayer = "com.google.android.exoplayer:exoplayer:${Versions.AndroidX.exoplayer}"
    }

    object Rive {
        const val rive = "app.rive:rive-android:${Versions.Rive.android}"
    }

    object Compose {
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val activity = "androidx.activity:activity-compose:${Versions.Compose.activity}"
        const val tvFoundation = "androidx.tv:tv-foundation:${Versions.Compose.tv}"
        const val tvMaterial = "androidx.tv:tv-material:${Versions.Compose.tv}"
        const val material3 = "androidx.compose.material3:material3:${Versions.Compose.material3}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.Compose.navigation}"
        const val coil = "io.coil-kt:coil-compose:${Versions.Compose.coil}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.Compose.viewModel}"
        const val bom = "androidx.compose:compose-bom:${Versions.Compose.bom}"
        const val contraintLayout = "androidx.constraintlayout:constraintlayout-compose:${Versions.Compose.constraintLayout}"
    }

    object Hilt {
        const val navigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.Hilt.compose_hilt}"
        const val daggerAndroid = "com.google.dagger:hilt-android:${Versions.Hilt.version}"
    }

    object Coroutine {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Coroutine.version}"
    }

    object OkHttp{
        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp.version}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.OkHttp.version}"
    }

    object Retrofit{
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.Retrofit.version}"
        const val retrofit_gson = "com.squareup.retrofit2:converter-gson:${Versions.Retrofit.version}"
    }

    object Gson{
        const val gson = "com.google.code.gson:gson:${Versions.Gson.version}"
    }

    object Google {
        const val guava = "com.google.guava:guava:${Versions.Google.guava}"
        const val zxing = "com.google.zxing:core:${Versions.Google.zxing}"
        const val fcm = "com.google.firebase:firebase-messaging:${Versions.Google.fcm}"
        const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.Google.firebaseBom}"
        const val firebaseCrashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx"
        const val firebaseAnallyticsKtx = "com.google.firebase:firebase-analytics-ktx"
        const val playServiceMeasurement = "com.google.android.gms:play-services-measurement-api:${Versions.Google.playServiceMeasurement}"

    }

    object Protobuf{
        const val grpcCore = "io.grpc:grpc-core:${Versions.Protobuf.grpcVersion}"
        const val grpcOkhttp = "io.grpc:grpc-okhttp:${Versions.Protobuf.grpcVersion}"
        const val grpcProtobufLite = "io.grpc:grpc-protobuf-lite:${Versions.Protobuf.grpcVersion}"
        const val kotlinStub = "io.grpc:grpc-kotlin-stub:${Versions.Protobuf.grpcKotlinVersion}"
        const val protobufKotlinLite = "com.google.protobuf:protobuf-kotlin-lite:${Versions.Protobuf.protobufVersion}"
    }
}

object ClassPaths{
    const val gradle = "com.android.tools.build:gradle:${Versions.Plugins.gradle}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.Hilt.version}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlin}"
    const val googleService = "com.google.gms:google-services:${Versions.Plugins.googleService}"
}

object Kapts {
    object Hilt {
        const val daggerHiltCompiler = "com.google.dagger:hilt-compiler:${Versions.Hilt.version}"
        const val daggerHiltAndroid = "com.google.dagger:hilt-android:${Versions.Hilt.version}"
        const val daggerHiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.Hilt.version}"
    }
}