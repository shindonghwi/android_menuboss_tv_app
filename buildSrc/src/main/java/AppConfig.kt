import org.gradle.api.JavaVersion

object AppConfig {
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33
    const val versionCode = 1
    const val versionName = "0.0.1"
    val javaVersion = JavaVersion.VERSION_11
    val jvmTarget = "11"
}

object DebugConfig {
    const val app_label = "MenuBossTV(DEV)"
    const val suffixName = ".dev"
    const val versionName = "-dev"
}

object ReleaseConfig {
    const val app_label = "MenuBossTV"
    const val suffixName = ""
    const val versionName = ""
}