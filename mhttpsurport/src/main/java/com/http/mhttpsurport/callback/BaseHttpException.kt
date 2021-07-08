package com.http.mhttpsurport.callback

import com.http.mhttpsurport.requestentity.ResponseEntity

open class BaseHttpException(
    val errorCode: String,
    val errorMessage: String,
    val realException: Throwable?
) : Exception(errorMessage) {

    companion object {

        /**
         * When network request process throw error.
         */
        const val CODE_ERROR_LOCAL_UNKNOWN = "-1001"

    }

    /**
     * Is service code return( code != successCode ) error.
     */
    val isServerCodeBadException: Boolean
        get() = this is ServerCodeBadException

    /**
     * Is it due to an exception thrown during the network request (like: the JSON returned by the server failed to parse).
     */
    val isLocalBadException: Boolean
        get() = this is LocalBadException

}

/**
 * API request success, but( code != successCode).
 */
class ServerCodeBadException(errorCode: String, errorMessage: String) :
    BaseHttpException(errorCode, errorMessage, null) {
    constructor(bean: ResponseEntity<*>) : this(
        bean.httpCode, bean.httpMsg ?: bean.httpErrorMsgTitle
    )
}

/**
 * It is due to exception throw during the network request.
 */
class LocalBadException(throwable: Throwable) : BaseHttpException(
    CODE_ERROR_LOCAL_UNKNOWN, throwable.message
        ?: "", throwable
)

/**
 * It is due to exception throw during the response interceptor.
 */
class InterceptorBadException(throwable: BaseInterceptorIOException) : BaseHttpException(
    throwable.message ?: "", throwable.cause?.message
        ?: "", throwable
)