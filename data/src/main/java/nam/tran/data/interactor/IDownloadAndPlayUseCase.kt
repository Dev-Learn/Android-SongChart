package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.DownloadData
import nam.tran.data.model.PlayerData

interface IDownloadAndPlayUseCase{
    val listSongDownload : LiveData<DownloadData>
    val songPlayer : LiveData<PlayerData>
    fun downloadMusic(id: Int, url: String, resume: Boolean)
    fun updateStatusDownload(id: Int,status : Int,isDownload : Boolean)
    fun removeTaskDownload(item: DownloadData?)
    fun updateSongDownloadCompleteNotUpdateUi(id : Int)
    fun playSong(name: String, id: Int, path: String?)
    fun stopSong(id: Int)
    fun pauseSong()
    fun updateSongStatus(playerData: PlayerData)
    fun getListIdPause() : List<DownloadData>
    fun pauseId() : Int
}