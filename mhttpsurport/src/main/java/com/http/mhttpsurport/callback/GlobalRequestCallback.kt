package com.http.mhttpsurport.callback

class GlobalRequestCallback<Data>(internal var onSuccess: ((Data) -> Unit)? = null,
                                  internal var onSuccessIO: (suspend (Data) -> Unit)? = null) : BaseCallback() {

    /**
     * This method is called when the network request succeeds, followed by onSuccessIo, then onFinally.
     */
    fun onSuccess(block: (data: Data) -> Unit) {
        this.onSuccess = block
    }

    /**
     * Time requirements do not open a child thread here.
     */
    fun onSuccessIO(block: suspend (Data) -> Unit) {
        this.onSuccessIO = block
    }

}