/*
 * Copyright (c) 2021 ASKEY Computer Corp. and/or its affiliates. All rights reserved.
 */
package com.http.mhttpsurport.ibasecoroutine

import kotlinx.coroutines.*

interface IBaseCoroutine {

    /**
     * This field is used to declare the scope of the coroutine in the global scope and is not bound to the life cycle.
     */
    val globalScope: CoroutineScope
        get() = GlobalScope

    val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val cpuDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    suspend fun <T> withMainTh(block: suspend CoroutineScope.() -> T): T {
        return withContext(mainDispatcher, block)
    }

    suspend fun <T> withIoTh(block: suspend CoroutineScope.() -> T): T {
        return withContext(ioDispatcher, block)
    }

    /**
     * This field is bound to the lifecycle.
     */
    val lifecycleMainCoroutine: CoroutineScope


    suspend fun <T> withNonCancellable(block: suspend CoroutineScope.() -> T): T {
        return withContext(NonCancellable, block)
    }


    /**
     * It's not associated with the life cycle.
     */

    fun launchMainGlobal(block: suspend CoroutineScope.() -> Unit): Job {
        return globalScope.launch(context = mainDispatcher, block = block)
    }

    fun <T> asyncMainGlobal(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return globalScope.async(context = mainDispatcher, block = block)
    }


    /**
     * Associations with life cycles.
     */
    fun launchMainLC(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleMainCoroutine.launch(context = mainDispatcher, block = block)
    }

    fun <T> asyncMainLC(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return lifecycleMainCoroutine.async(context = mainDispatcher, block = block)
    }

}