package com.orot.menuboss_tv

import androidx.multidex.MultiDexApplication
import androidx.navigation.NavDestination
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.Rum
import com.datadog.android.rum.RumConfiguration
import com.datadog.android.rum.tracking.ComponentPredicate
import com.datadog.android.rum.tracking.NavigationViewTrackingStrategy
import com.datadog.android.rum.tracking.ViewTrackingStrategy
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MenuBossTVApp : MultiDexApplication(){

    override fun onCreate() {
        super.onCreate()

        val clientToken = "pubb8168ab481c460b707160dcebf634f0b"
        val environmentName = "dev.env"
        val appVariantName = BuildConfig.FLAVOR

        val configuration = Configuration.Builder(
            clientToken = clientToken,
            env = environmentName,
            variant = appVariantName
        )
            .useSite(DatadogSite.US5)
            .build()
        Datadog.initialize(this, configuration, TrackingConsent.GRANTED)

        val applicationId = "97275dcb-f02d-45c4-8d1c-01f8c3052744"
        val rumConfiguration = RumConfiguration.Builder(applicationId)
            .trackUserInteractions()
            .trackLongTasks(250L)
            .build()
        Rum.enable(rumConfiguration)
    }
}
