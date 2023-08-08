package com.orot.menuboss_tv.data.utils

import okio.IOException

class ApiException(message: String) : IOException(message)

class NoInternetException(message: String) : IOException(message)