package dev.tran.nam.chart.chartsong.view.main.singer.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import dev.tran.nam.chart.chartsong.controller.NotificationController
import nam.tran.data.interactor.ISingerUseCase
import nam.tran.data.model.*
import nam.tran.data.model.core.state.Resource
import tran.nam.core.viewmodel.BaseFragmentViewModel
import javax.inject.Inject

class SingerViewModel @Inject internal constructor(
    application: Application, private val iSingerUseCase: ISingerUseCase
    , private var mNotificationController: NotificationController
) : BaseFragmentViewModel(application) {

    var results: LiveData<Resource<List<Song>>> = iSingerUseCase.listSongSinger
    var resultListDownload: LiveData<DownloadData> = iSingerUseCase.listSongDownload
    var resultPlay : LiveData<PlayerData> = iSingerUseCase.songPlayer

    fun resource(): Resource<*>? {
        return results.value
    }

    fun getData(idSinger: Int, folderPath: String) {
        iSingerUseCase.getData(idSinger, folderPath)
    }

    fun downloadSong(weekSong: Song, isResume: Boolean = false) {
        iSingerUseCase.downloadMusic(weekSong.id, weekSong.link_local, isResume)
    }

    fun updateStatus(id: Int, status: Int, isDownload: Boolean = false) {
        iSingerUseCase.updateStatusDownload(id, status, isDownload)
    }

    fun playSong(name: String, id: Int, path: String?) {
        iSingerUseCase.playSong(name, id, path)
    }

    fun stopSong() {
        iSingerUseCase.stopSong()
    }

    fun pauseSong() {
        iSingerUseCase.pauseSong()
    }

    fun songClick(item: Song, folder: String) {
        when (item.songStatus) {
            SongStatus.NONE_STATUS -> {
                item.songStatus = SongStatus.DOWNLOADING
                item.downloadStatus = DownloadStatus.RUNNING
                downloadSong(item)
            }
            SongStatus.DOWNLOADING, SongStatus.ERROR -> {
                if (item.downloadStatus == DownloadStatus.RUNNING) {
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

    fun downloadStatusClick(item: Song) {
        when (item.downloadStatus) {
            DownloadStatus.RUNNING -> {
                item.downloadStatus = DownloadStatus.PAUSE
                updateStatus(item.id, DownloadStatus.PAUSE, true)
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
            mNotificationController.updatePlayerSong(
                playerData.id,
                playerData.name,
                playerData.progress,
                playerData.total
            )

        if (playerData.idOld != null)
            mNotificationController.clearNotification(playerData.idOld!!)
    }
}
