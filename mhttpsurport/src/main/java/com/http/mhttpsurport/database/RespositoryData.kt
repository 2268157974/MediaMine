package com.http.mhttpsurport.database

import com.http.mhttpsurport.baseinterface.IUIHttpEvent
import com.http.mhttpsurport.callback.GlobalRequestCallback
import com.http.mhttpsurport.callback.ServerCodeBadException
import com.http.mhttpsurport.requestentity.ResponseEntity
import kotlinx.coroutines.Job

abstract class RespositoryHttpData<Api : Any>(
    iUiHttpEvent: IUIHttpEvent?,
    apiServiceClass: Class<Api>
) : BaseRespositoryData<Api>(iUiHttpEvent, apiServiceClass) {
    /**
     * @param isShowLoading No loading box is used by default.
     * @param baseUrl @null User-defined ，else Use dynamic URLs
     * @param isShowErrorToast @false Do not display the wrong global error toast.
     */
    fun <BaseData> getRemoteData(
        apiFunc: suspend Api.() -> ResponseEntity<BaseData>,
        isShowLoading: Boolean = false,
        isShowErrorToast: Boolean = true,
        baseUrl: String = "",
        callbackFunc: (GlobalRequestCallback<BaseData>.() -> Unit)? = null
    ): Job {
        return launchMainLC {
            val callback =
                if (callbackFunc == null) null else GlobalRequestCallback<BaseData>().apply {
                    callbackFunc.invoke(this)
                }
            try {
                if (isShowLoading) {
                    showLoading(coroutineContext[Job])
                }

                callback?.onStart?.invoke()
                val invokeResponse: ResponseEntity<BaseData>?
                try {
                    invokeResponse = apiFunc.invoke(getApiService(baseUrl))
                    if (invokeResponse.httpIsFailed) {
                        throw ServerCodeBadException(invokeResponse)
                    }
                } catch (e: Throwable) {
                    handleException(e, callback, isShowErrorToast)
                    return@launchMainLC
                }
                parseResponse(callback, invokeResponse.httpData)
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                try {
                    callback?.onFinally?.invoke()
                } finally {
                    if (isShowLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private suspend fun <BaseData> parseResponse(
        callback: GlobalRequestCallback<BaseData>?,
        httpData: BaseData
    ) {
        callback?.let {
            withNonCancellable {
                callback.onSuccess?.let {
                    withMainTh {
                        it.invoke(httpData)
                    }
                }
                callback.onSuccessIO?.let {
                    withIoTh {
                        it.invoke(httpData)
                    }
                }
            }
        }
    }


}