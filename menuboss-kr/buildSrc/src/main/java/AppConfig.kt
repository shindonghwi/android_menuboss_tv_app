import org.gradle.api.JavaVersion

object AppConfig {
    const val compileSdk = 34
    const val minSdk = 26
    const val targetSdk = 34
    const val versionCode = 2
    const val versionName = "1.0.1"
    val javaVersion = JavaVersion.VERSION_11
    val jvmTarget = "11"
}

object DebugConfig {
    const val app_label = "메뉴보스(DEV)"
    const val suffixName = ".dev"
    const val versionName = "-dev"
}

object ReleaseConfig {
    const val app_label = "메뉴보스TV"
    const val suffixName = ""
    const val versionName = ""
}