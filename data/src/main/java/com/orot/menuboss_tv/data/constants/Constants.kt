package com.orot.menuboss_tv.data.constants

import com.orot.menuboss_tv.data.BuildConfig


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

const val API_VERSION = "v1"