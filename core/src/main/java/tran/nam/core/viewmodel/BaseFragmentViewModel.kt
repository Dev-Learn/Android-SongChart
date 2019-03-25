package tran.nam.core.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import nam.tran.data.Logger
import java.lang.ref.WeakReference

open class BaseFragmentViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    @Volatile
    var mViewLoadingWeakReference: WeakReference<IViewLifecycle>? = null

    protected inline fun<reified V: IViewLifecycle> view(): V? {
        if (mViewLoadingWeakReference == null || mViewLoadingWeakReference?.get() == null)
            return null
        return V::class.java.cast(mViewLoadingWeakReference?.get())
    }

    fun onAttach(viewLoading: IViewLifecycle) {
        Logger.w("BaseFragmentViewModel : onAttach()")
        mViewLoadingWeakReference = WeakReference(viewLoading)
        viewLoading.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreated() {
        Logger.w("BaseFragmentViewModel : onCreated()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
        Logger.w("BaseFragmentViewModel : onStart()")
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
        Logger.w("BaseFragmentViewModel : onResume()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
        Logger.w("BaseFragmentViewModel : onPause()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
        Logger.w("BaseFragmentViewModel : onStop()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy() {
        Logger.w("BaseFragmentViewModel : onDestroy()")
        val viewWeakReference = this.mViewLoadingWeakReference
        if (viewWeakReference != null) {
            val view = viewWeakReference.get()
            view?.lifecycle?.removeObserver(this)
        }
    }
}
