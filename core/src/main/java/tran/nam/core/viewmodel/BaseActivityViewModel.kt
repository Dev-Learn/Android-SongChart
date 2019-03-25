package tran.nam.core.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import android.os.Bundle
import java.lang.ref.WeakReference

@Suppress("unused", "UNUSED_PARAMETER")
open class BaseActivityViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    @Volatile
    var mViewLoadingWeakReference: WeakReference<IViewLifecycle>? = null

    protected inline fun<reified V: IViewLifecycle> view(): V? {
        if (mViewLoadingWeakReference == null || mViewLoadingWeakReference?.get() == null)
            return null
        return V::class.java.cast(mViewLoadingWeakReference?.get())
    }

    open fun onCreated(viewLoading: IViewLifecycle) {
        mViewLoadingWeakReference = WeakReference(viewLoading)
        viewLoading.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun create() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun start() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun resume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun pause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun stop() {
    }

    open fun onRestoreInstanceState(savedInstanceState: Bundle?) {

    }

    open fun onSaveInstanceState(outState: Bundle?) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun cleanup() {
        val viewWeakReference = this.mViewLoadingWeakReference
        if (viewWeakReference != null) {
            val view = viewWeakReference.get()
            view?.lifecycle?.removeObserver(this)
        }
    }
}
