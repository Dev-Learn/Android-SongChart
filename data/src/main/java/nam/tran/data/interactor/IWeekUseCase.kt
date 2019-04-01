package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.DownloadData
import nam.tran.data.model.PlayerData
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.Resource

interface IWeekUseCase{
    val listWeekChart : LiveData<Resource<List<WeekChart>>>
    val listSongWeek : LiveData<Resource<List<WeekSong>>>
    val listSongDownload : LiveData<DownloadData>
    val songPlayer : LiveData<PlayerData>
    fun getData(position: Int? = null, pathFolder: String? = null)
    fun getDataExist(position: Int)
    fun downloadMusic(id: Int, url: String, resume: Boolean)
    fun updateStatusDownload(id: Int,status : Int,isDownload : Boolean)
    fun removeTaskDownload(item: DownloadData?)
    fun updateSongDownloadCompleteNotUpdateUi(id : Int)
    fun playSong(name: String, id: Int, path: String?)
    fun stopSong(id: Int)
    fun pauseSong()
    fun updateSongStatus(playerData: PlayerData)
}