package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.Resource
import java.util.*

interface IWeekUseCase{
    val listWeekChart : LiveData<Resource<List<WeekChart>>>
    val listSongWeek : LiveData<Resource<List<WeekSong>>>
    val listSongDownload : LiveData<Vector<WeekSong>>
    fun getData(position: Int? = null, pathFolder: String? = null)
    fun getDataExist(position: Int)
    fun downloadMusic(song : WeekSong)
}