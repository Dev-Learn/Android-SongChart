package dev.tran.nam.chart.chartsong.view.main.chart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import dev.tran.nam.chart.chartsong.controller.NotificationController
import nam.tran.data.interactor.IWeekUseCase
import nam.tran.data.model.*
import nam.tran.data.model.DownloadStatus.*
import nam.tran.data.model.core.state.Resource
import tran.nam.core.viewmodel.BaseFragmentViewModel
import javax.inject.Inject

class ChartSongViewModel @Inject internal constructor(
    application: Application,
    private val iWeekUseCase: IWeekUseCase,
    private var mNotificationController: NotificationController
) : BaseFragmentViewModel(application) {

    var results: LiveData<Resource<List<WeekChart>>> = iWeekUseCase.listWeekChart
    var resultChild: LiveData<Resource<List<Song>>> = iWeekUseCase.listSongWeek
    var resultListDownload: LiveData<DownloadData> = iWeekUseCase.listSongDownload
    var resultPlay : LiveData<PlayerData> = iWeekUseCase.songPlayer
    var mPosition = -1

    fun resource(): Resource<*>? {
        return results.value
    }

    fun resourceChild(): Resource<*>? {
        return resultChild.value
    }

    fun getData(position : Int? = null, pathFolder: String? = null) {
        if (position == null){
            mPosition = 0
        }
        iWeekUseCase.getData(position,pathFolder)
    }

    fun getDataExist(position: Int) {
        iWeekUseCase.getDataExist(position)
    }

    fun downloadSong(weekSong: Song,isResume : Boolean = false){
        iWeekUseCase.downloadMusic(weekSong.id,weekSong.link_local,isResume)
    }

    fun updateStatus(id: Int,status : Int,isDownload : Boolean = false){
        iWeekUseCase.updateStatusDownload(id,status,isDownload)
    }

    fun playSong(name: String, id: Int, path: String?) {
        iWeekUseCase.playSong(name,id,path)
    }

    fun stopSong() {
        iWeekUseCase.stopSong()
    }

    fun pauseSong() {
        iWeekUseCase.pauseSong()
    }

    fun songClick(item: Song,folder: String) {
        when (item.songStatus) {
            SongStatus.NONE_STATUS -> {
                item.songStatus = SongStatus.DOWNLOADING
                item.downloadStatus = RUNNING
                downloadSong(item)
            }
            SongStatus.DOWNLOADING, SongStatus.ERROR -> {
                if (item.downloadStatus == RUNNING){
                    item.songStatus = SongStatus.CANCELING_DOWNLOAD
                }
                updateStatus(item.id, SongStatus.CANCEL_DOWNLOAD)
            }
            SongStatus.PLAY -> {
                playSong(item.name, item.id, folder)
            }
            SongStatus.PLAYING -> {
                pauseSong()
            }
            SongStatus.PAUSE_SONG -> {
                playSong(item.name, item.id, folder)
            }
            else -> {
            }
        }
    }

    fun downloadStatusClick(item: Song) = when (item.downloadStatus) {
        RUNNING -> {
            item.downloadStatus = PAUSE
            updateStatus(item.id, PAUSE, true)
        }
        PAUSE -> {
            item.downloadStatus = RUNNING
            downloadSong(item, true)
        }
        NONE -> {
        }
        else -> {
        }
    }

    fun updateNotification(playerData: PlayerData) {
        if (playerData.songStatus == SongStatus.PLAY)
            mNotificationController.clearNotification(playerData.id)
        else
            mNotificationController.updatePlayerSong(playerData.id, playerData.name, playerData.progress, playerData.total)

        if (playerData.idOld != null)
            mNotificationController.clearNotification(playerData.idOld!!)
    }

    override fun onCleared() {
        mNotificationController.clearAllNotification()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
