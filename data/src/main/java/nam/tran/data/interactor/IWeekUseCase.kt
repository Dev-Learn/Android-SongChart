package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.*
import nam.tran.data.model.core.state.Resource

interface IWeekUseCase : IDownloadAndPlayUseCase{
    val listWeekChart : LiveData<Resource<List<WeekChart>>>
    val listSongWeek : LiveData<Resource<List<Song>>>
    fun getData(position: Int? = null, pathFolder: String? = null)
    fun getDataExist(position: Int)
}