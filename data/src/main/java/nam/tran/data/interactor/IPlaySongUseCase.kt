package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.SongPlayerData

interface IPlaySongUseCase {
    val songplayer : LiveData<SongPlayerData>
    fun playSong(name: String, id: Int,pathFolder : String?)
    fun pauseSong(id : Int)
}