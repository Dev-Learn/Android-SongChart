package dev.tran.nam.chart.chartsong.view.splash

import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.ActivitySplashBinding
import tran.nam.core.view.BaseActivity

class SplashActivity : BaseActivity() {

    private var mViewDataBinding: ActivitySplashBinding? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = Runnable { }

    override fun layoutId(): Int {
        return R.layout.activity_splash
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        mViewDataBinding = DataBindingUtil.setContentView(this, layoutId())

        handler = Handler()
        handler!!.postDelayed(runnable, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler!!.removeCallbacks(runnable)
        handler = null
        runnable = null
    }
}
