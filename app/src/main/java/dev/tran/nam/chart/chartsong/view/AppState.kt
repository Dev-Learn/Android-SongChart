package dev.tran.nam.chart.chartsong.view


import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dev.tran.nam.chart.chartsong.di.component.AppComponent
import dev.tran.nam.chart.chartsong.di.component.DaggerAppComponent
import nam.tran.data.BuildConfig
import nam.tran.data.Logger
import javax.inject.Inject

class AppState : MultiDexApplication(), Application.ActivityLifecycleCallbacks,
    HasActivityInjector {

    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null
        @Inject set

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent!!.inject(this)
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        Logger.enter("onActivityCreated : " + activity.componentName)
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.enter("onActivityStarted : " + activity.componentName)
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.enter("onActivityResumed : " + activity.componentName)
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.enter("onActivityPaused : " + activity.componentName)
    }

    override fun onActivityStopped(activity: Activity) {
        Logger.enter("onActivityStopped : " + activity.componentName)
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
        Logger.enter("onActivitySaveInstanceState : " + activity.componentName)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.enter("onActivityDestroyed : " + activity.componentName)
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }
}