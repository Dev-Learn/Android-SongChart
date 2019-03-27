package nam.tran.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import nam.tran.data.BR
import nam.tran.data.model.DownloadStatus.PAUSE
import nam.tran.data.model.SongStatus.PLAY

data class WeekSong(
    val song: Song, val position: Int, val hierarchical: Int, val hierarchical_number: Int?
    , @SongStatus var _songStatus: Int = PLAY, @DownloadStatus var _downloadStatus: Int = PAUSE, var _progressDownload : Int
) : BaseObservable() {

    @SongStatus
    var songStatus: Int
        @Bindable get() = _songStatus
        set(value) {
            _songStatus = value
            notifyPropertyChanged(BR.songStatus)
        }

    @DownloadStatus
    var downloadStatus: Int
        @Bindable get() = _downloadStatus
        set(value) {
            _downloadStatus = value
            notifyPropertyChanged(BR.downloadStatus)
        }

    var progressDownload : Int
        @Bindable get() = _progressDownload
        set(value) {
            _progressDownload = value
            notifyPropertyChanged(BR.progressDownload)
        }

    override fun equals(other: Any?): Boolean {
        return other is WeekSong && other.song.id == song.id
    }

    override fun hashCode(): Int {
        var result = song.hashCode()
        result = 31 * result + position
        result = 31 * result + hierarchical
        result = 31 * result + (hierarchical_number ?: 0)
        result = 31 * result + _songStatus
        result = 31 * result + _downloadStatus
        return result
    }
}