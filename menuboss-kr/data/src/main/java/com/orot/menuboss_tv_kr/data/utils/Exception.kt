package com.orot.menuboss_tv_kr.data.utils

import okio.IOException

class ApiException(message: String) : IOException(message)

class NoInternetException(message: String) : IOException(message)

class JobCloseException(message: String) : IOException(message)