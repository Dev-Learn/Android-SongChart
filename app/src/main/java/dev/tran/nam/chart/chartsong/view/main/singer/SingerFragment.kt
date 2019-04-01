package dev.tran.nam.chart.chartsong.view.main.singer

import android.os.Bundle
import android.view.View

import tran.nam.core.view.mvvm.BaseFragmentVM
import dev.tran.nam.chart.chartsong.view.main.singer.viewmodel.SingerViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dev.tran.nam.chart.chartsong.databinding.FragmentSingerBinding

import dev.tran.nam.chart.chartsong.R

class SingerFragment : BaseFragmentVM<FragmentSingerBinding, SingerViewModel>() {

    override fun initViewModel(factory: ViewModelProvider.Factory?) {
        mViewModel = ViewModelProviders.of(this, factory).get(SingerViewModel::class.java)
    }


    override fun layoutId(): Int {
        return R.layout.fragment_singer
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewDataBinding?.viewModel = mViewModel
    }
}
