package nam.tran.data.model

import nam.tran.data.model.DownloadStatus.RUNNING
import nam.tran.data.model.SongStatus.DOWNLOADING
import nam.tran.data.model.core.state.ErrorResource

data class DownloadData(
    val id: Int,
    var progress: Int = 0, @SongStatus var songStatus: Int = DOWNLOADING, @DownloadStatus var downloadStatus: Int = RUNNING,
    var errorResource: ErrorResource? = null
){

    override fun equals(other: Any?): Boolean {
        return other is DownloadData && other.id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + progress
        result = 31 * result + songStatus
        result = 31 * result + downloadStatus
        result = 31 * result + (errorResource?.hashCode() ?: 0)
        return result
    }
}