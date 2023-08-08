package com.orot.menuboss_tv.data.constants

import com.orot.menuboss_tv.core.BuildConfig

val BASE_URL = if (BuildConfig.DEBUG) {
    "https://dev-tv-api.menuboss.tv"
} else {
    "https://tv-api.menuboss.tv"
}

const val API_VERSION = "v1"