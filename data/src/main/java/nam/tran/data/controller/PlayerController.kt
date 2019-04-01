package nam.tran.data.controller

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.Logger
import nam.tran.data.model.PlayerData
import nam.tran.data.model.SongStatus.*
import java.lang.IllegalStateException
import javax.inject.Inject

class PlayerController @Inject constructor() : IPlayerController {

    private val mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var folderPath: String

    private val mHandler = Handler()
    private val _listPlayerNotUpdateUi = mutableListOf<Int>()

    private var mPlayerData : PlayerData? = null
    private var isPause = false
    private var currentPosition : Int = 0

    private val _player = MutableLiveData<PlayerData>()
    override val player: LiveData<PlayerData>
        get() = _player

    override fun checkPlayerNotUpdateUI(id : Int): Boolean {
        if (_listPlayerNotUpdateUi.contains(id)){
            _listPlayerNotUpdateUi.remove(id)
            return true
        }
        return false
    }

    /**
     * Background Runnable thread
     */
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            if (mPlayer.isPlaying){
                mPlayerData?.progress = mPlayer.currentPosition
                mPlayerData?.total = mPlayer.duration
                _player.postValue(mPlayerData)
                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100)
            }
        }
    }

    override fun playSong(name: String, id: Int, pathFolder: String?) {
        try {
            if (!mPlayer.isPlaying && isPause && id == mPlayerData?.id){
                if (currentPosition != 0){
                    mPlayerData?.songStatus = PLAYING
                    mPlayer.seekTo(currentPosition)
                    mHandler.postDelayed(mUpdateTimeTask, 100)
                    mPlayer.start()
                }
                return
            }else{
                mPlayer.reset()
            }
        }catch (e : IllegalStateException){
            Logger.debug(e)
        }

        returnDefaultSongPlayer(id,name)
        pathFolder?.run {
            folderPath = this
        }

        mPlayer.setDataSource(folderPath.plus("/").plus(id).plus(".mp3"))
        mPlayer.setOnCompletionListener {
            stopSong(mPlayerData!!.id)
        }
        mPlayer.setOnPreparedListener {
            mHandler.postDelayed(mUpdateTimeTask, 100)
            it.start()
        }
        mPlayer.prepareAsync();
    }

    override fun pauseSong() {
        if (mPlayer.isPlaying){
            isPause = true
            currentPosition = mPlayer.currentPosition
            mPlayer.pause()
            mHandler.removeCallbacks(mUpdateTimeTask)
            mPlayerData?.songStatus = PAUSE_SONG
            _player.value = mPlayerData
        }
    }

    override fun stopSong(id: Int) {
        isPause = false
        currentPosition = 0
        mPlayer.reset()
        mHandler.removeCallbacks(mUpdateTimeTask)
        mPlayerData?.idOld = null
        mPlayerData?.songStatus = PLAY
        _player.value = mPlayerData
    }

    private fun returnDefaultSongPlayer(id: Int, name: String) {
        isPause = false
        currentPosition = 0
        mHandler.removeCallbacks(mUpdateTimeTask)
        if (mPlayerData == null)
            mPlayerData = PlayerData(id)
        else{
            if (mPlayerData!!.id != id){
                mPlayerData!!.idOld = mPlayerData!!.id
                mPlayerData!!.id = id
            }
        }
        mPlayerData!!.name = name
        mPlayerData!!.songStatus = PLAYING
        mPlayerData!!.progress = 0
    }

    override fun updateListPlayerUI(playerData: PlayerData) {
        _listPlayerNotUpdateUi.add(playerData.idOld!!)
    }

}