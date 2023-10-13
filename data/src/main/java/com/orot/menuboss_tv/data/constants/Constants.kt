package com.orot.menuboss_tv.data.constants

import com.orot.menuboss_tv.data.BuildConfig


val BASE_URL = if (BuildConfig.DEBUG) {
    "https://dev-screen-api.themenuboss.com"
} else {
    "https://tv-api.menuboss.tv"
}

const val API_VERSION = "v1"