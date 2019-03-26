package dev.tran.nam.chart.chartsong.view.main.chart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import nam.tran.data.interactor.IWeekUseCase
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.Resource
import nam.tran.data.model.core.state.Status.*
import tran.nam.core.viewmodel.BaseFragmentViewModel
import javax.inject.Inject

class ChartSongViewModel @Inject internal constructor(
    application: Application,
    private val iWeekUseCase: IWeekUseCase
) : BaseFragmentViewModel(application) {

    var results: LiveData<Resource<List<WeekChart>>> = iWeekUseCase.listWeekChart
    var resultChild: LiveData<Resource<List<WeekSong>>> = iWeekUseCase.listSongWeek


    fun resource(): Resource<*>? {
        return results.value
    }

    fun resourceChild(): Resource<*>? {
        return resultChild.value
    }

    fun getData(position : Int? = null) {
        iWeekUseCase.getData(position)
    }

    fun cancleGetData() {

    }

    fun getDataExist(position: Int) {
        iWeekUseCase.getDataExist(position)
    }
}
