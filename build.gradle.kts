plugins {
    id("com.android.application") version Versions.Plugins.gradle apply false
    id("com.android.library") version Versions.Plugins.gradle apply false
    id("org.jetbrains.kotlin.android") version Versions.Plugins.kotlin apply false
    id("com.google.gms.google-services") version Versions.Plugins.googleService apply false
    id("com.google.dagger.hilt.android") version Versions.Plugins.hilt apply false
}
