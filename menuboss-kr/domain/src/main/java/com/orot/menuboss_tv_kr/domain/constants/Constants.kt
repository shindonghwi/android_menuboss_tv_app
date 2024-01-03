package com.orot.menuboss_tv_kr.domain.constants

import com.orot.menuboss_tv_kr.domain.BuildConfig


val BASE_URL = if (BuildConfig.DEBUG) {
    "https://dev-app-api.menuboss.kr"
} else {
    "https://app-api.menuboss.kr"
}

val GRPC_BASE_URL = if (BuildConfig.DEBUG) {
    "dev-screen-grpc.menuboss.kr"
} else {
    "screen-grpc.menuboss.kr"
}

val WEB_LOGIN_URL = if (BuildConfig.DEBUG) {
    "https://dev-www.menuboss.kr/login"
} else {
    "https://www.menuboss.kr/login"
}

val MENUBOSS_AMAZON_STORE_URL = "amzn://apps/android?p=com.orot.menuboss_tv_kr"
val MENUBOSS_GOOGLE_STORE_URL = "market://details?id=com.orot.menuboss_tv_kr"

const val API_VERSION = "v1"