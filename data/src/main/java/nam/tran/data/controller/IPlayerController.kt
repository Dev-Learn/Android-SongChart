package nam.tran.data.controller

import androidx.lifecycle.LiveData
import nam.tran.data.model.PlayerData

interface IPlayerController {
    val player : LiveData<PlayerData>
    fun checkPlayerNotUpdateUI(id : Int) : Boolean
    fun playSong(name: String, id: Int,pathFolder : String?)
    fun pauseSong()
    fun stopSong(id: Int)
    fun updateListPlayerUI(playerData: PlayerData)
    fun pauseId() : Int
}