package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.WeekChart
import nam.tran.data.model.core.state.Resource

interface IWeekUseCase{
    val listWeekChart : LiveData<Resource<WeekChart>>
    fun getData(weekId : Int? = null)
}