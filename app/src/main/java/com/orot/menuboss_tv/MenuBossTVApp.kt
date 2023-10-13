package com.orot.menuboss_tv

import android.util.Log
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MenuBossTVApp : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        val variantInfo = "Flavor: ${BuildConfig.FLAVOR}, Build Type: ${BuildConfig.BUILD_TYPE}"
        Log.w("Asdsadsdasad", "onCreate: $variantInfo", )
    }
}

