package com.http.mhttpsurport.callback

open class BaseCallback(internal var onStart: (() -> Unit)? = null,
                               internal var onCancelled: (() -> Unit)? = null,
                               internal var onFailed: ((BaseHttpException) -> Unit)? = null,
                               internal var onFailToast: (() -> Boolean) = { true },
                               internal var onFinally: (() -> Unit)? = null) {

    /**
     * after show loading and before web request invoke
     */
    fun onStart(block: () -> Unit) {
        this.onStart = block
    }

    fun onCancelled(block: () -> Unit) {
        this.onCancelled = block
    }

    /**
     * When web request failed ,before @onFinally invoke
     */
    fun onFailed(block: (BaseHttpException) -> Unit) {
        this.onFailed = block
    }

    /**
     * Show web request failed reason if its (@param block) true
     */
    fun onFailToast(block: () -> Boolean) {
        this.onFailToast = block
    }

    /**
     * When after web request finally and hide loading
     */
    fun onFinally(block: () -> Unit) {
        this.onFinally = block
    }

}