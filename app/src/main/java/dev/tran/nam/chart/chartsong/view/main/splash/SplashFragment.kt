package dev.tran.nam.chart.chartsong.view.main.splash

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.Navigation

import tran.nam.core.view.BaseFragment

import dev.tran.nam.chart.chartsong.R

class SplashFragment : BaseFragment() {


    override fun layoutId(): Int {
        return R.layout.fragment_splash
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Handler().postDelayed({
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_splashFragment_to_chartSongFragment)
        },1000)
    }
}
