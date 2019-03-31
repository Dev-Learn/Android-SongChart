package nam.tran.data.interactor

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.Logger
import nam.tran.data.model.SongPlayerData
import nam.tran.data.model.SongStatus.*
import java.lang.IllegalStateException
import javax.inject.Inject


class PlaySongUseCase @Inject constructor() : IPlaySongUseCase {

    private val mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var folderPath: String

    private val _songplayer = MutableLiveData<SongPlayerData>()
    override val songplayer: LiveData<SongPlayerData>
        get() = _songplayer

    private val mHandler = Handler()

    private var songPlayerData : SongPlayerData? = null
    private var isPause = false
    private var currentPosition : Int = 0

    /**
     * Background Runnable thread
     */
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            if (mPlayer.isPlaying){
                songPlayerData?.progress = mPlayer.currentPosition
                songPlayerData?.total = mPlayer.duration
                _songplayer.postValue(songPlayerData)
                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100)
            }
        }
    }

    override fun playSong(name: String, id: Int, pathFolder: String?) {
        try {
            if (!mPlayer.isPlaying && isPause && id == songPlayerData?.id){
                if (currentPosition != 0){
                    songPlayerData?.songStatus = PLAYING
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
            stopSong(songPlayerData!!.id)
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
            songPlayerData?.songStatus = PAUSE_SONG
            _songplayer.value = songPlayerData
        }
    }

    override fun stopSong(id: Int) {
        isPause = false
        currentPosition = 0
        mPlayer.reset()
        mHandler.removeCallbacks(mUpdateTimeTask)
        songPlayerData?.idOld = null
        songPlayerData?.songStatus = PLAY
        _songplayer.value = songPlayerData
    }

    private fun returnDefaultSongPlayer(id: Int, name: String) {
        isPause = false
        currentPosition = 0
        mHandler.removeCallbacks(mUpdateTimeTask)
        if (songPlayerData == null)
            songPlayerData = SongPlayerData(id)
        else{
            if (songPlayerData!!.id != id){
                songPlayerData!!.idOld = songPlayerData!!.id
                songPlayerData!!.id
            }
        }
        songPlayerData!!.name = name
        songPlayerData!!.songStatus = PLAYING
        songPlayerData!!.progress = 0
    }

}