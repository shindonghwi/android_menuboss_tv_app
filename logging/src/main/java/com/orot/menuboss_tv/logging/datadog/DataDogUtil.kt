package com.orot.menuboss_tv.logging.datadog

import com.datadog.android.rum.GlobalRumMonitor

object DataDogLoggingUtil {

    fun startView(
        viewKey: String,
        viewName: String,
        viewAttributes: Map<String, Any> = emptyMap()
    ) {
        GlobalRumMonitor.get().startView(viewKey, viewName, viewAttributes)
    }

    fun stopView(
        viewKey: String,
        viewAttributes: Map<String, Any> = emptyMap()
    ) {
        GlobalRumMonitor.get().stopView(viewKey, viewAttributes)
    }


}