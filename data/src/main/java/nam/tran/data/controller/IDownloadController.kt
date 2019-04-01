package nam.tran.data.controller

import androidx.lifecycle.LiveData
import nam.tran.data.model.DownloadData
import java.io.IOException

interface IDownloadController {
    val listDownload : LiveData<DownloadData>
    fun downloadMusic(id: Int, url: String, resume: Boolean, folderPath: String)
    fun updateStatusDownload(id: Int, status: Int, isDownload: Boolean, folderPath: String)
    fun updateSongDownloadCompleteNotUpdateUi(id: Int)
    fun checkItemNotUpdateUI(id: Int) : Boolean
    fun removeTaskDownload(item: DownloadData?)
}