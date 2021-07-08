package com.http.mhttpsurport.baseUi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.http.mhttpsurport.baseinterface.*
import kotlinx.coroutines.CoroutineScope

open class BaseMViewModel : ViewModel(), IVMEvent {
    override val showLoadingEventLD = MutableLiveData<ShowLoadingEvent>()

    override val dismissLoadingEventLD = MutableLiveData<DismissLoadingEvent>()

    override val showToastEventLD = MutableLiveData<ShowToastEvent>()

    override val finishViewEventLD = MutableLiveData<FinishViewEvent>()

    override val showErrorEventLD = MutableLiveData<ShowErrorEvent>()

    override val lifecycleMainCoroutine: CoroutineScope
        get() = viewModelScope
}

