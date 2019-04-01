package nam.tran.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import nam.tran.data.BR
import nam.tran.data.model.core.state.ErrorResource

data class Song(val id : Int, val name : String, val image : String
                , val link128 : String, val link320 : String, val lossless : String, val link_local : String
                , val singer : Singer?, @SongStatus var _songStatus: Int = SongStatus.NONE_STATUS, @DownloadStatus var _downloadStatus: Int = DownloadStatus.NONE,
                var _progressDownload: Int,
                var errorResource: ErrorResource? = null,
                var _enableButton : Boolean = true) : BaseObservable(){

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