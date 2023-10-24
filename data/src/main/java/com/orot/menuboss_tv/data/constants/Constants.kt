package com.orot.menuboss_tv.data.constants

import com.orot.menuboss_tv.data.BuildConfig


val BASE_URL = if (BuildConfig.DEBUG) {
    "https://dev-app-api.themenuboss.com"
} else {
    "https://app-api.themenuboss.com"
}

const val API_VERSION = "v1"