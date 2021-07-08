package com.http.mhttpsurport.baseUi

import android.content.Context
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.http.mhttpsurport.baseinterface.IBaseEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class BaseMActivity : AppCompatActivity(), IBaseEvent {

    override val lifecycleMainCoroutine: CoroutineScope
        get() = lifecycleScope

    override val mLifecycleOwner: LifecycleOwner
        get() = this

    override val mContext: Context?
        get() = this

    override fun showError(t: Throwable?) {
    }

    override fun showToast(msg: String) {
    }

    //dismiss view
    override fun dismissLoading() {
    }

    override fun showLoadingView(show: Boolean, job: Job?) {
        //loading view
    }

    override fun finishView() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoading()
    }

    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isShouldHideInput(view, ev)) {
                hideSoftInput(view?.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(
        v: View?,
        event: MotionEvent
    ): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = (left
                    + v.getWidth())
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val im =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(
                token,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}