package nam.tran.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import nam.tran.data.BR
import nam.tran.data.model.DownloadStatus.PAUSE
import nam.tran.data.model.SongStatus.DOWNLOADING
import nam.tran.data.model.SongStatus.PLAY
import nam.tran.data.model.core.state.ErrorResource
import java.io.File

data class Song(
    val id: Int,
    val idWeek: Int?,
    val name: String,
    val image: String,
    val link128: String,
    val link320: String,
    val lossless: String,
    val link_local: String,
    val singer: Singer?,
    var position: Int?,
    val length: Long? = null,
    @SongStatus var _songStatus: Int = SongStatus.NONE_STATUS, @DownloadStatus var _downloadStatus: Int = DownloadStatus.NONE,
    var _progressDownload: Int,
    var _errorResource: ErrorResource? = null,
    var _enableButton: Boolean = true
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

    var progressDownload: Int
        @Bindable get() = _progressDownload
        set(value) {
            _progressDownload = value
            notifyPropertyChanged(BR.progressDownload)
        }

    var errorResource: ErrorResource?
        @Bindable get() = _errorResource
        set(value) {
            _errorResource = value
            notifyPropertyChanged(BR.errorResource)
        }

    fun statusDownload(file: File): Int {
        if (file.exists()) {
            if (length != null) {
                if (length == file.length()) {
                    songStatus = PLAY
                    return 1
                } else {
                    songStatus = DOWNLOADING
                    downloadStatus = PAUSE
                    progressDownload = (file.length() * 100 / length).toInt()
                    return 2
                }
            } else {
                songStatus = PLAY
                return 1
            }
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        return other is Song && other.id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + link128.hashCode()
        result = 31 * result + link320.hashCode()
        result = 31 * result + lossless.hashCode()
        result = 31 * result + link_local.hashCode()
        result = 31 * result + (singer?.hashCode() ?: 0)
        return result
    }
}