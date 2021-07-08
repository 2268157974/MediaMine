package com.http.mhttpsurport.database

import android.util.LruCache
import com.http.mhttpsurport.baseinterface.IUIHttpEvent
import com.http.mhttpsurport.callback.*
import com.http.mhttpsurport.ibasecoroutine.IBaseCoroutine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

abstract class BaseRespositoryData<Api : Any>(
    val iUiHttpEvent: IUIHttpEvent?,
    private val apiServiceClass: Class<Api>
) : IBaseCoroutine {

    companion object {

        /**
         * Api cache
         */
        private val apiServiceCache = LruCache<String, Any>(30)

        /**
         * Retrofit cache
         */
        private val retrofitCache = LruCache<String, Retrofit>(3)

        /**
         * default http client
         */
        private val createDefaultHttpClient by lazy {
            OkHttpClient.Builder()
                .readTimeout(15L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .connectTimeout(15L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(
                    HttpLoggingInterceptor(
                        HttpLoggingInterceptor.Logger { message ->
                        }).setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()
        }

        private fun createDefaultRetrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .client(createDefaultHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    }

    /**
     * The coroutine scope bound to the lifecycle.
     */
    override val lifecycleMainCoroutine = iUiHttpEvent?.lifecycleMainCoroutine ?: GlobalScope

    /**
     * This field is implemented by a subclass to get BaseURL.
     */
    protected abstract val baseUrl: String

    /**
     * Allow subclasses to implement the logic for creating Retrofit themselves.
     */
    protected open fun createRetrofit(baseUrl: String): Retrofit {
        return createDefaultRetrofit(baseUrl)
    }

    protected open fun generateBaseUrl(baseUrl: String): String {
        if (baseUrl.isNotBlank()) {
            return baseUrl
        }
        return this.baseUrl
    }

    fun getApiService(baseUrl: String = ""): Api {
        return getApiService(generateBaseUrl(baseUrl), apiServiceClass)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getApiService(baseUrl: String, apiServiceClazz: Class<Api>): Api {
        val key = baseUrl + apiServiceClazz.canonicalName
        @Suppress("UNCHECKED_CAST") val get = apiServiceCache.get(key)?.let {
            it as? Api
        }
        if (get != null) {
            return get
        }
        val retrofit = retrofitCache.get(baseUrl) ?: (createRetrofit(baseUrl).apply {
            retrofitCache.put(baseUrl, this)
        })
        val apiService = retrofit.create(apiServiceClazz)
        apiServiceCache.put(key, apiService)
        return apiService
    }

    protected fun handleException(
        throwable: Throwable,
        callback: BaseCallback?,
        showErrorToast: Boolean
    ) {
        if (callback == null) {
            return
        }
        if (throwable is CancellationException) {
            callback.onCancelled?.invoke()
            return
        }
        val exception = generateBaseExceptionReal(throwable)
        if (exceptionHandle(exception)) {
            callback.onFailed?.invoke(exception)
            if (callback.onFailToast()) {
                val error = exceptionFormat(exception)
                if (showErrorToast && error.isNotBlank()) {
                    showToast(error)
                }
            }
        }
    }

    internal fun generateBaseExceptionReal(throwable: Throwable): BaseHttpException {
        return generateBaseException(throwable).apply {
            exceptionRecord(this)
        }
    }

    /**
     * self define Throwable
     */
    protected open fun generateBaseException(throwable: Throwable): BaseHttpException {
        return when (throwable) {
            is BaseHttpException -> {
                throwable
            }
            is BaseInterceptorIOException -> {
                InterceptorBadException(throwable)
            }
            else -> {
                LocalBadException(throwable)
            }
        }
    }

    /**
     * Used to control whether an onFail callback is performed when an exception is thrown by an external relay,
     * and when it returns true, otherwise no callback is performed.
     */
    protected open fun exceptionHandle(httpException: BaseHttpException): Boolean {
        return true
    }

    /**
     * Used to feed back the exception in the network request process to the outside for recording.
     */
    protected open fun exceptionRecord(throwable: Throwable) {

    }

    /**
     * Used to format BaseException so that the TOAST prompts an error message when the request fails.
     */
    protected open fun exceptionFormat(httpException: BaseHttpException): String {
        return when (httpException.realException) {
            null -> {
                httpException.errorMessage
            }
            is ConnectException, is SocketTimeoutException, is UnknownHostException -> {
                "The connection has timed out. Please check your network Settings."
            }
            else -> {
//                "The request procedure throws an exception: " + httpException.errorMessage
                httpException.errorMessage
            }
        }
    }

    protected fun showLoading(job: Job?) {
        iUiHttpEvent?.showLoadingView(true, job)
    }

    protected fun dismissLoading() {
        iUiHttpEvent?.dismissLoading()
    }

    abstract fun showToast(msg: String)

}