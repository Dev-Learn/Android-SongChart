package dev.tran.nam.chart.chartsong.view.main.chart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import nam.tran.data.interactor.IPlaySongUseCase
import nam.tran.data.interactor.IWeekUseCase
import nam.tran.data.model.DownloadData
import nam.tran.data.model.SongPlayerData
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.Resource
import tran.nam.core.viewmodel.BaseFragmentViewModel
import javax.inject.Inject

class ChartSongViewModel @Inject internal constructor(
    application: Application,
    private val iWeekUseCase: IWeekUseCase,
    private val iPlaySongUseCase: IPlaySongUseCase
) : BaseFragmentViewModel(application) {

    var results: LiveData<Resource<List<WeekChart>>> = iWeekUseCase.listWeekChart
    var resultChild: LiveData<Resource<List<WeekSong>>> = iWeekUseCase.listSongWeek
    var resultListDownload: LiveData<DownloadData> = iWeekUseCase.listSongDownload
    var resultSongPlay : LiveData<SongPlayerData> = iPlaySongUseCase.songplayer
    var listDownloadComplete = mutableListOf<Int>()

    fun resource(): Resource<*>? {
        return results.value
    }

    fun resourceChild(): Resource<*>? {
        return resultChild.value
    }

    fun getData(position : Int? = null, pathFolder: String? = null) {
        iWeekUseCase.getData(position,pathFolder)
    }

    fun getDataExist(position: Int) {
        iWeekUseCase.getDataExist(position,listDownloadComplete)
    }

    fun downloadSong(weekSong: WeekSong,isResume : Boolean = false){
        iWeekUseCase.downloadMusic(weekSong.song.id,weekSong.song.link_local,isResume)
    }

    fun updateStatus(id: Int,status : Int,isDownload : Boolean = false){
        iWeekUseCase.updateStatusDownload(id,status,isDownload)
    }

    fun removeTaskDownload(item: DownloadData?) {
        iWeekUseCase.removeTaskDownload(item)
    }

    fun playSong(name: String, id: Int, path: String?) {
        iPlaySongUseCase.playSong(name,id,path)
    }

    fun stopSong(id: Int) {
        iPlaySongUseCase.stopSong(id)
    }

    fun pauseSong() {
        iPlaySongUseCase.pauseSong()
    }
}
