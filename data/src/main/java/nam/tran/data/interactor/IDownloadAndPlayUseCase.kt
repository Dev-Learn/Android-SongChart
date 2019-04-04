package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.DownloadData
import nam.tran.data.model.PlayerData
import nam.tran.data.model.Song

interface IDownloadAndPlayUseCase{
    val listSongDownload : LiveData<DownloadData>
    val songPlayer : LiveData<PlayerData>
    fun updatePauseDownload(song : Song)
    fun downloadMusic(id: Int, url: String, resume: Boolean)
    fun updateStatusDownload(id: Int,status : Int,isDownload : Boolean)
    fun removeTaskDownload(item: DownloadData?)
    fun playSong(name: String, id: Int, path: String?)
    fun stopSong()
    fun pauseSong()
    fun getListIdPause() : List<DownloadData>
    fun pauseId() : Int
    fun releaseController()
}