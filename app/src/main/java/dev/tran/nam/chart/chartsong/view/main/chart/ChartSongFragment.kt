package dev.tran.nam.chart.chartsong.view.main.chart

import android.os.Bundle
import android.view.View

import tran.nam.core.view.mvvm.BaseFragmentVM
import dev.tran.nam.chart.chartsong.view.main.chart.viewmodel.ChartSongViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dev.tran.nam.chart.chartsong.databinding.FragmentChartSongBinding

import dev.tran.nam.chart.chartsong.R

class ChartSongFragment : BaseFragmentVM<FragmentChartSongBinding, ChartSongViewModel>() {

    override fun initViewModel(factory: ViewModelProvider.Factory?) {
        mViewModel = ViewModelProviders.of(this, factory).get(ChartSongViewModel::class.java)
    }


    override fun layoutId(): Int {
        return R.layout.fragment_chart_song
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewDataBinding?.viewModel = mViewModel
    }
}
