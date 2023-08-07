import org.gradle.api.JavaVersion

object AppConfig {
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33
    const val versionCode = 1
    const val versionName = "0.0.1"
    val javaVersion = JavaVersion.VERSION_17
    val jvmTarget = "17"
    val packageName = "com.orot.menuboss_tv"
}

object DebugConfig {
    const val app_label = "MenuBoss(DEV)"
    const val suffixName = ".dev"
    const val versionName = "-dev"
}

object ReleaseConfig {
    const val app_label = "MenuBoss"
    const val suffixName = ""
    const val versionName = ""
}