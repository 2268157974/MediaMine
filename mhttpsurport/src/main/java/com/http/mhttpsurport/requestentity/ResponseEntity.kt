package com.http.mhttpsurport.requestentity

interface ResponseEntity<Data> {
    /**
     * In data returned by the server, the identifier used to identify whether the current request was successful.
     */
    val httpCode: String

    /**
     * In data returned by the server, a string used to identify the current status of the request, usually to store the reason for the failure.
     */
    val httpMsg: String?

    /**
     * In data returned by the server, a string used to identify the current status of the request, usually to store the simple reason(title) for the failure.
     */
    val httpErrorMsgTitle: String

    /**
     * The actual data returned by the server.
     */
    val httpData: Data

    /**
     * Leave it to the external to determine whether the current interface request was successful.
     */
    val httpIsSuccess: Boolean

    val httpIsFailed: Boolean
        get() = !httpIsSuccess


}