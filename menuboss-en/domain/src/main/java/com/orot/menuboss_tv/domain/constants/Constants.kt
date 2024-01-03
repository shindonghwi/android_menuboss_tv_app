package com.orot.menuboss_tv.domain.constants

import com.orot.menuboss_tv.domain.BuildConfig


val BASE_URL = if (BuildConfig.DEBUG) {
    "https://dev-app-api.themenuboss.com"
} else {
    "https://app-api.themenuboss.com"
}

val GRPC_BASE_URL = if (BuildConfig.DEBUG) {
    "dev-screen-grpc.themenuboss.com"
} else {
    "screen-grpc.themenuboss.com"
}

val WEB_LOGIN_URL = if (BuildConfig.DEBUG) {
    "https://dev-www.themenuboss.com/login"
} else {
    "https://www.themenuboss.com/login"
}

val MENUBOSS_AMAZON_STORE_URL = "amzn://apps/android?p=com.orot.menuboss_tv"
val MENUBOSS_GOOGLE_STORE_URL = "market://details?id=com.orot.menuboss_tv"

const val API_VERSION = "v1"