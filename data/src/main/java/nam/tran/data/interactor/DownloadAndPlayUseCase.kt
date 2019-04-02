package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.controller.IDownloadController
import nam.tran.data.controller.IPlayerController
import nam.tran.data.model.DownloadData
import nam.tran.data.model.PlayerData

open class DownloadAndPlayUseCase constructor(private val iPlayerController: IPlayerController
                                              , private val iDownloadController: IDownloadController) : IDownloadAndPlayUseCase{
    lateinit var folderPath: String

    override val listSongDownload: LiveData<DownloadData>
        get() = iDownloadController.listDownload

    override val songPlayer: LiveData<PlayerData>
        get() = iPlayerController.player

    override fun downloadMusic(id: Int, url: String, resume: Boolean) {
        iDownloadController.downloadMusic(id,url,resume,folderPath)
    }

    override fun removeTaskDownload(item: DownloadData?) {
        iDownloadController.removeTaskDownload(item)
    }

    override fun updateSongDownloadCompleteNotUpdateUi(id: Int) {
        iDownloadController.updateSongDownloadCompleteNotUpdateUi(id)
    }

    override fun updateStatusDownload(id: Int, status: Int, isDownload: Boolean) {
        iDownloadController.updateStatusDownload(id,status,isDownload,folderPath)
    }

    override fun playSong(name: String, id: Int, path: String?) {
        iPlayerController.playSong(name,id,path)
    }

    override fun pauseSong() {
        iPlayerController.pauseSong()
    }

    override fun stopSong(id: Int) {
        iPlayerController.stopSong(id)
    }

    override fun updateSongStatus(playerData: PlayerData) {
        iPlayerController.updateListPlayerUI(playerData)
    }

    override fun getListIdPause(): List<DownloadData> {
        return iDownloadController.getListIdPause()
    }

    override fun pauseId(): Int {
        return iPlayerController.pauseId()
    }
}