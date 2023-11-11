
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(ClassPaths.googleService)
        classpath(ClassPaths.hilt)
        classpath(ClassPaths.kotlin)
        classpath(ClassPaths.gradle)
        classpath("com.datadoghq:dd-sdk-android-gradle-plugin:1.12.0")
    }
}

plugins {
    id("com.android.application") version Versions.Plugins.gradle apply false
    id("com.android.library") version Versions.Plugins.gradle apply false
    id("org.jetbrains.kotlin.android") version Versions.Plugins.kotlin apply false
    id("com.google.gms.google-services") version Versions.Plugins.googleService apply false
    id("com.google.dagger.hilt.android") version Versions.Plugins.hilt apply false
    id("com.google.firebase.crashlytics") version Versions.Plugins.crashlytics apply false
    id("com.google.protobuf") version Versions.Plugins.protobuf apply false
    id("com.datadoghq.dd-sdk-android-gradle-plugin") version "1.12.0" apply false
}
