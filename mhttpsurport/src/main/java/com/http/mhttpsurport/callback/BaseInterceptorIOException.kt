package com.http.mhttpsurport.callback

import java.io.IOException

open class BaseInterceptorIOException(code: String = "-1", message: String? = "") : IOException(code, Throwable(message))