package com.http.mhttpsurport.baseinterface

import android.content.Context
import androidx.lifecycle.*
import com.http.mhttpsurport.ibasecoroutine.IBaseCoroutine
import kotlinx.coroutines.Job

interface IBaseEvent : IUIHttpEvent {
    val mContext: Context?

    val mLifecycleOwner: LifecycleOwner

    fun <VM> getGlobalViewModel(clazz: Class<VM>,
                                factory: ViewModelProvider.Factory? = null,
                                initializer: (VM.(lifecycleOwner: LifecycleOwner) -> Unit)? = null)
            : Lazy<VM> where VM : ViewModel, VM : IVMEvent {
        return lazy {
            getGlobalViewModelImpl(clazz, factory, initializer)
        }
    }

    fun <VM> getGlobalViewModelImpl(clazz: Class<VM>,
                                    factory: ViewModelProvider.Factory? = null,
                                    initializer: (VM.(lifecycleOwner: LifecycleOwner) -> Unit)? = null)
            : VM where VM : ViewModel, VM : IVMEvent {
        return when (val localValue = mLifecycleOwner) {
            is ViewModelStoreOwner -> {
                if (factory == null) {
                    ViewModelProvider(localValue).get(clazz)
                } else {
                    ViewModelProvider(localValue, factory).get(clazz)
                }
            }
            else -> {
                factory?.create(clazz) ?: clazz.newInstance()
            }
        }.apply {
            mActionImpl(this)
            initializer?.invoke(this, mLifecycleOwner)
        }
    }

    fun <VM> mActionImpl(viewModel: VM) where VM : ViewModel, VM : IVMEvent {

        viewModel.dismissLoadingEventLD.observe(mLifecycleOwner, Observer {
            this@IBaseEvent.dismissLoading()
        })
        viewModel.showToastEventLD.observe(mLifecycleOwner, Observer {
            if (it.message.isNotBlank()) {
                this@IBaseEvent.showToast(it.message)
            }
        })
        viewModel.finishViewEventLD.observe(mLifecycleOwner, Observer {
            this@IBaseEvent.finishView()
        })

        viewModel.showLoadingEventLD.observe(mLifecycleOwner, Observer {
            this@IBaseEvent.showLoadingView(it.isJob,it.job)
        })
        viewModel.showErrorEventLD.observe(mLifecycleOwner, Observer {
            this@IBaseEvent.showError(it.t)
        })
    }
}

interface IUIHttpEvent : IBaseCoroutine {

    fun showError(t: Throwable?)

    fun dismissLoading()

    fun showToast(msg: String)

    fun showLoadingView(show: Boolean, job: Job? = null)

    fun finishView()

}


interface IVMEvent : IUIHttpEvent {

    val showLoadingEventLD: MutableLiveData<ShowLoadingEvent>

    val dismissLoadingEventLD: MutableLiveData<DismissLoadingEvent>

    val showToastEventLD: MutableLiveData<ShowToastEvent>

    val finishViewEventLD: MutableLiveData<FinishViewEvent>

    val showErrorEventLD: MutableLiveData<ShowErrorEvent>

    override fun showLoadingView(show: Boolean, job: Job?) {
        showLoadingEventLD.value = ShowLoadingEvent(show, job)
    }

    override fun dismissLoading() {
        dismissLoadingEventLD.value = DismissLoadingEvent
    }

    override fun showToast(msg: String) {
        showToastEventLD.value = ShowToastEvent(msg)
    }

    override fun finishView() {
        finishViewEventLD.value = FinishViewEvent
    }

    override fun showError(t: Throwable?) {
        showErrorEventLD.value = ShowErrorEvent(t)
    }

}


