
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
}

gradle.taskGraph.whenReady {
    allTasks.forEach { task ->
        if (
            task.name.contains("DevRelease", ignoreCase = true) ||
            task.name.contains("ProdDebug", ignoreCase = true)
        ) {
            task.enabled = false
        }
    }
}
