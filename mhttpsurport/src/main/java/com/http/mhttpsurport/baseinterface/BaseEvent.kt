package com.http.mhttpsurport.baseinterface

import kotlinx.coroutines.Job


open class BaseActionEvent

class ShowToastEvent(val message: String) : BaseActionEvent()

class ShowErrorEvent(val t: Throwable?) : BaseActionEvent()

class ShowLoadingEvent(val isJob: Boolean, val job: Job?) : BaseActionEvent()

object DismissLoadingEvent : BaseActionEvent()

object FinishViewEvent : BaseActionEvent()