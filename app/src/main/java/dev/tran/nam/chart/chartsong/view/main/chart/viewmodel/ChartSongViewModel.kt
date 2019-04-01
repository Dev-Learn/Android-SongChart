package dev.tran.nam.chart.chartsong.view.main.chart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import dev.tran.nam.chart.chartsong.controller.NotificationController
import nam.tran.data.interactor.IWeekUseCase
import nam.tran.data.model.*
import nam.tran.data.model.core.state.Resource
import tran.nam.core.viewmodel.BaseFragmentViewModel
import javax.inject.Inject

class ChartSongViewModel @Inject internal constructor(
    application: Application,
    private val iWeekUseCase: IWeekUseCase,
    private var mNotificationController: NotificationController
) : BaseFragmentViewModel(application) {

    var results: LiveData<Resource<List<WeekChart>>> = iWeekUseCase.listWeekChart
    var resultChild: LiveData<Resource<List<WeekSong>>> = iWeekUseCase.listSongWeek
    var resultListDownload: LiveData<DownloadData> = iWeekUseCase.listSongDownload
    var resultPlay : LiveData<PlayerData> = iWeekUseCase.songPlayer

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
        iWeekUseCase.getDataExist(position)
    }

    fun downloadSong(weekSong: WeekSong,isResume : Boolean = false){
        iWeekUseCase.downloadMusic(weekSong.song.id,weekSong.song.link_local,isResume)
    }

    fun updateStatus(id: Int,status : Int,isDownload : Boolean = false){
        iWeekUseCase.updateStatusDownload(id,status,isDownload)
    }

    fun playSong(name: String, id: Int, path: String?) {
        iWeekUseCase.playSong(name,id,path)
    }

    fun stopSong(id: Int) {
        iWeekUseCase.stopSong(id)
    }

    fun pauseSong() {
        iWeekUseCase.pauseSong()
    }

    fun updateSongStatus(playerData: PlayerData) {
        iWeekUseCase.updateSongStatus(playerData)
    }

    fun updateSongDownloadCompleteNotUi(id: Int) {
        iWeekUseCase.updateSongDownloadCompleteNotUpdateUi(id)
    }

    fun songClick(item: WeekSong,folder: String) {
        when (item.songStatus) {
            SongStatus.NONE_STATUS -> {
                item.songStatus = SongStatus.DOWNLOADING
                item.downloadStatus = DownloadStatus.RUNNING
                downloadSong(item)
            }
            SongStatus.DOWNLOADING, SongStatus.ERROR -> {
                if (item.downloadStatus == DownloadStatus.RUNNING){
                    item.songStatus = SongStatus.CANCELING_DOWNLOAD
                }
                updateStatus(item.song.id, SongStatus.CANCEL_DOWNLOAD)
            }
            SongStatus.PLAY -> {
                playSong(item.song.name, item.song.id, folder)
            }
            SongStatus.PLAYING -> {
                pauseSong()
            }
            SongStatus.PAUSE_SONG -> {
                playSong(item.song.name, item.song.id, folder)
            }
            else -> {
            }
        }
    }

    fun downloadStatusClick(item: WeekSong) {
        when (item.downloadStatus) {
            DownloadStatus.RUNNING -> {
                item.downloadStatus = DownloadStatus.PAUSE
                updateStatus(item.song.id, DownloadStatus.PAUSE, true)
            }
            DownloadStatus.PAUSE -> {
                item.downloadStatus = DownloadStatus.RUNNING
                downloadSong(item, true)
            }
            DownloadStatus.NONE -> {
            }
            else -> {
            }
        }
    }

    fun updateNotification(playerData: PlayerData) {
        if (playerData.songStatus == SongStatus.PLAY)
            mNotificationController.clearNotification(playerData.id)
        else
            mNotificationController.updatePlayerSong(playerData.id, playerData.name, playerData.progress, playerData.total)
    }
}
