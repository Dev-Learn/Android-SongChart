package dev.tran.nam.chart.chartsong.view.main.chart.viewmodel

import android.app.Application

import javax.inject.Inject

import tran.nam.core.viewmodel.BaseFragmentViewModel
import nam.tran.data.model.core.state.Resource
import androidx.lifecycle.LiveData

class ChartSongViewModel @Inject internal constructor(application: Application) : BaseFragmentViewModel(application) {

    var results: LiveData<Resource<*>>? = null

    fun resource(): Resource<*>? {
        return results?.value
    }
}
