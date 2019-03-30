package nam.tran.data.interactor

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.model.SongPlayerData
import nam.tran.data.model.SongStatus.PLAYING
import javax.inject.Inject


class PlaySongUseCase @Inject constructor() : IPlaySongUseCase {

    private val mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var folderPath: String

    private val _songplayer = MutableLiveData<SongPlayerData>()
    override val songplayer: LiveData<SongPlayerData>
        get() = _songplayer

    private val mHandler = Handler()

    private val songPlayer = SongPlayerData()

    /**
     * Background Runnable thread
     */
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val totalDuration = mPlayer.duration
            val currentDuration = mPlayer.currentPosition

            val progress = currentDuration * 100 / totalDuration
            songPlayer.progress = progress

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100)
        }
    }

    override fun playSong(name: String, id: Int, pathFolder: String?) {
        returnDefaultSongPlayer(id)
        pathFolder?.run {
            folderPath = this
        }
        if (mPlayer.isPlaying) {
            mPlayer.release()
        }

        mPlayer.setDataSource(folderPath.plus("/").plus(id).plus(".mp3"))
        mPlayer.setOnCompletionListener {
            mPlayer.release()
        }
        mPlayer.prepare()
        mPlayer.setOnPreparedListener {
            it.start()
        }
    }

    override fun pauseSong(id: Int) {

    }

    private fun returnDefaultSongPlayer(id: Int) {
        songPlayer.id = id
        songPlayer.songStatus = PLAYING
        songPlayer.progress = 0
    }

}