package nam.tran.data.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.Logger
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.DownloadData
import nam.tran.data.model.DownloadStatus
import nam.tran.data.model.DownloadStatus.PAUSE
import nam.tran.data.model.SongStatus
import nam.tran.data.model.SongStatus.*
import nam.tran.data.model.core.state.ErrorResource
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class DownloadController @Inject constructor(private val appExecutors: AppExecutors) : IDownloadController{

    private val currentDownloadMap: MutableMap<Int, DownloadData> = ConcurrentHashMap()
    private var listDownloadNotUpdateUI = mutableListOf<Int>()

    val _listDownload = MutableLiveData<DownloadData>()
    override val listDownload: LiveData<DownloadData>
        get() = _listDownload

    override fun removeTaskDownload(item: DownloadData?) {
        Logger.debug("removeTaskDownload : $item")
        if (currentDownloadMap.containsValue(item))
            currentDownloadMap.remove(item?.id)
    }

    override fun downloadMusic(id: Int, url: String, resume: Boolean,folderPath: String) {
        var fileDownLoad = DownloadData(id)
        if (!currentDownloadMap.contains(id)){
            currentDownloadMap[id] = fileDownLoad
        }else{
            fileDownLoad = currentDownloadMap.getValue(id)
            fileDownLoad.songStatus = SongStatus.DOWNLOADING
            fileDownLoad.downloadStatus = DownloadStatus.RUNNING
        }

        appExecutors.networkIO().execute {
            val pathFile = folderPath.plus("/").plus(id).plus(".mp3")
            var fileLenght: Long = 0
            val file = File(pathFile)
            if (file.exists()){
                fileLenght = file.length()
            }
            Logger.debug("downloadMusic : fileLenght - $fileLenght")
            var isCancel: Boolean = false
            var isPause: Boolean = false
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                if (fileLenght > 0){
                    connection.setRequestProperty("Range", "bytes=$fileLenght -")
                }
                connection.connect()

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                var totalfileLength = connection.contentLength
                Logger.debug("downloadMusic : totalfileLength - $totalfileLength")
                if (fileLenght > 0)
                    totalfileLength += fileLenght.toInt()

                // download the file
                val input = connection.inputStream
                val output = FileOutputStream(pathFile,fileLenght > 0)

                val buffer = ByteArray(100)
                var total = 0
                var length: Int

                //https://stackoverflow.com/questions/6237079/resume-http-file-download-in-java

                while (input.read(buffer, 0, 100).let { length = it; length > 0 }) {
                    if (fileDownLoad.songStatus == SongStatus.CANCEL_DOWNLOAD) {
                        isCancel = true
                        break
                    }
                    if (fileDownLoad.downloadStatus == DownloadStatus.PAUSE) {
                        isPause = true
                        break
                    }
                    total += length
                    if (totalfileLength > 0) {
                        fileDownLoad.progress = ((fileLenght + total) * 100 / totalfileLength).toInt()
                        _listDownload.postValue(fileDownLoad)
                    }
                    output.write(buffer, 0, length)
                }

                output.close()
                input.close()

                Logger.debug(isCancel)
                Logger.debug(isPause)
                if (isCancel) {
                    cancelComplete(fileDownLoad,pathFile)
                } else if (isPause) {

                } else {
                    downloadComplete(fileDownLoad)
                }
            } catch (e: IOException) {
                Logger.debug(e)
                downloadError(fileDownLoad, e)
            }
        }
    }

    override fun updateStatusDownload(id: Int, status: Int,isDownload : Boolean,folderPath: String) {
        if (currentDownloadMap.containsKey(id)) {
            val fileDownLoad = currentDownloadMap[id]
            fileDownLoad?.apply {
                if (isDownload)
                    downloadStatus = status
                else{
                    songStatus = status
                    if (songStatus == SongStatus.CANCEL_DOWNLOAD && downloadStatus == DownloadStatus.PAUSE){
                        val listdata = _listDownload.value
                        listdata?.run {
                            val pathFile = folderPath.plus("/").plus(id).plus(".mp3")
                            cancelComplete(fileDownLoad, pathFile)
                        }
                    }
                }
            }
        }
    }

    override fun updateSongDownloadCompleteNotUpdateUi(id: Int) {
        listDownloadNotUpdateUI.add(id)
    }

    override fun checkItemNotUpdateUI(id: Int) : Boolean{
        if (listDownloadNotUpdateUI.contains(id)){
            listDownloadNotUpdateUI.remove(id)
            return true
        }
        return false
    }

    private fun downloadError(
        fileDownLoad: DownloadData,
        error: IOException
    ) {
        appExecutors.mainThread().execute {
            fileDownLoad.songStatus = SongStatus.ERROR
            fileDownLoad.downloadStatus = DownloadStatus.PAUSE
            fileDownLoad.errorResource = ErrorResource(error.message)
            _listDownload.value = fileDownLoad
        }
    }

    private fun downloadComplete(
        fileDownLoad: DownloadData
    ) {
        appExecutors.mainThread().execute {
            removeTaskDownload(fileDownLoad)
            fileDownLoad.songStatus = SongStatus.PLAY
            fileDownLoad.downloadStatus = DownloadStatus.NONE
            _listDownload.value = fileDownLoad
        }
    }

    private fun cancelComplete(
        fileDownLoad: DownloadData,
        pathFile: String
    ) {
        val file = File(pathFile)
        if (file.exists()) {
            val result = file.delete()
            Logger.debug("cancelComplete : delete - $result")
        }
        appExecutors.mainThread().execute {
            fileDownLoad.songStatus = SongStatus.NONE_STATUS
            fileDownLoad.downloadStatus = DownloadStatus.NONE
            fileDownLoad.progress = 0
            Logger.debug("cancelComplete : fileDownLoad - $fileDownLoad")
            removeTaskDownload(fileDownLoad)
            _listDownload.value = fileDownLoad
        }
    }

    override fun getListIdPause(): List<DownloadData> {
        val listId = mutableListOf<DownloadData>()
        for ((k,v) in currentDownloadMap){
            if ((v.songStatus == DOWNLOADING && v.downloadStatus == PAUSE) || (v.songStatus == ERROR && v.downloadStatus == PAUSE)){
                listId.add(v)
            }
        }
        return listId
    }
}