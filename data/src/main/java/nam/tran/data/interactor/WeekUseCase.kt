package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.Logger
import nam.tran.data.api.IApi
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.DownloadData
import nam.tran.data.model.DownloadStatus.*
import nam.tran.data.model.SongStatus.*
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.ErrorResource
import nam.tran.data.model.core.state.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


class WeekUseCase @Inject internal constructor(private val appExecutors: AppExecutors, private val iApi: IApi) :
    IWeekUseCase {

    private var isCancle = false
    private var currentPosition = 0
    private lateinit var folderPath: String
    private val currentDownloadMap: MutableMap<Int, DownloadData> = ConcurrentHashMap()

    private val _listWeekChart = MutableLiveData<Resource<List<WeekChart>>>()
    override val listWeekChart: LiveData<Resource<List<WeekChart>>>
        get() = _listWeekChart

    private val _listSongWeek = MediatorLiveData<Resource<List<WeekSong>>>()
    override val listSongWeek: LiveData<Resource<List<WeekSong>>>
        get() = _listSongWeek

    private val _listSongDownload = MutableLiveData<DownloadData>()
    override val listSongDownload: LiveData<DownloadData>
        get() = _listSongDownload

    override fun getData(position: Int?, pathFolder: String?) {
        pathFolder?.run {
            folderPath = this
        }
        if (position == null) {
            _listSongWeek.value = null
            _listWeekChart.value = Resource.loading()
            iApi.getCharMusic().enqueue(object : Callback<List<WeekChart>> {
                override fun onFailure(call: Call<List<WeekChart>>, t: Throwable) {
                    _listWeekChart.value = Resource.error(ErrorResource(t.message, t.hashCode()), retry = {
                        getData(position, pathFolder)
                    })
                }

                override fun onResponse(call: Call<List<WeekChart>>, response: Response<List<WeekChart>>) {
                    if (response.isSuccessful) {
                        val dataResponse = response.body()
                        if (dataResponse?.isNotEmpty() == true) {
                            dataResponse[0].isChoose = true
                            _listWeekChart.value = Resource.success(dataResponse)
                            getSongWeek(0, dataResponse)
                        }
                    } else {
                        _listWeekChart.value = Resource.error(
                            ErrorResource(
                                JSONObject(response.errorBody()?.string()).getString("message"),
                                response.code()
                            ), retry = {
                                getData(position, pathFolder)
                            }
                        )
                    }
                }
            })
        } else {
            val data = _listWeekChart.value
            data?.data?.run {
                isCancle = false
                getSongWeek(position, this)
            }

        }
    }

    private fun getSongWeek(position: Int, listDataWeekChart: List<WeekChart>) {
        synchronized(currentPosition) {
            currentPosition = position
        }
        _listSongWeek.value = Resource.loading()
        iApi.getListSongWeek(listDataWeekChart[position].id).enqueue(object : Callback<List<WeekSong>> {
            override fun onFailure(call: Call<List<WeekSong>>, t: Throwable) {
                if (!isCancle)
                    _listSongWeek.value = Resource.error(ErrorResource(t.message, t.hashCode()), retry = {
                        getSongWeek(position, listDataWeekChart)
                    })
            }

            override fun onResponse(call: Call<List<WeekSong>>, response: Response<List<WeekSong>>) {
                if (response.isSuccessful) {
                    val listWeekSong = response.body()
                    listWeekSong?.run {
                        for (song in this) {
                            val file = File(folderPath.plus("/").plus(song.song.id).plus(".mp3"))
                            print(file)
                            if (file.exists()) {
                                song.songStatus = PLAY
                            }
                        }
                        listDataWeekChart[position].listWeekSong = this
                        _listWeekChart.value = Resource.success(listDataWeekChart)
                        if (!isCancle && currentPosition == position)
                            _listSongWeek.value = Resource.success(this)
                    }
                } else {
                    if (!isCancle)
                        _listSongWeek.value = Resource.error(
                            ErrorResource(
                                JSONObject(response.errorBody()?.string()).getString("message"),
                                response.code()
                            ), retry = {
                                getSongWeek(position, listDataWeekChart)
                            }
                        )
                }
            }

        })
    }

    override fun getDataExist(position: Int, listDownloadComplete: MutableList<Int>) {
        isCancle = true
        _listSongWeek.value = Resource.loading()
        appExecutors.diskIO().execute {
            val data = _listWeekChart.value
            data?.data?.run {
                val listSongWeek = this[position].listWeekSong
                for (item in listSongWeek){
                    if (listDownloadComplete.contains(item.song.id)){
                        item.songStatus = PLAY
                        item.downloadStatus = NONE
                        listDownloadComplete.remove(item.song.id)
                    }
                }
                _listSongWeek.postValue(Resource.success(listSongWeek))
            }
        }
    }

    override fun removeTaskDownload(item: DownloadData?) {
        Logger.debug("removeTaskDownload : $item")
        if (currentDownloadMap.containsValue(item))
            currentDownloadMap.remove(item?.id)
    }

    override fun downloadMusic(id: Int, url: String, resume: Boolean) {
        var fileDownLoad = DownloadData(id)
        if (!currentDownloadMap.contains(id)){
            currentDownloadMap[id] = fileDownLoad
        }else{
            fileDownLoad = currentDownloadMap.getValue(id)
            fileDownLoad.songStatus = DOWNLOADING
            fileDownLoad.downloadStatus = RUNNING
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
                    if (fileDownLoad.songStatus == CANCEL_DOWNLOAD) {
                        isCancel = true
                        break
                    }
                    if (fileDownLoad.downloadStatus == PAUSE) {
                        isPause = true
                        break
                    }
                    total += length
                    if (totalfileLength > 0) {
                        fileDownLoad.progress = ((fileLenght + total) * 100 / totalfileLength).toInt()
                        _listSongDownload.postValue(fileDownLoad)
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

    override fun updateStatusDownload(id: Int, status: Int,isDownload : Boolean) {
        if (currentDownloadMap.containsKey(id)) {
            val fileDownLoad = currentDownloadMap[id]
            fileDownLoad?.apply {
                if (isDownload)
                    downloadStatus = status
                else{
                    songStatus = status
                    if (songStatus == CANCEL_DOWNLOAD && downloadStatus == PAUSE){
                        val listdata = listSongDownload.value
                        listdata?.run {
                            val pathFile = folderPath.plus("/").plus(id).plus(".mp3")
                            cancelComplete(fileDownLoad, pathFile)
                        }
                    }
                }
            }
        }
    }

    private fun downloadError(
        fileDownLoad: DownloadData,
        error: IOException
    ) {
        appExecutors.mainThread().execute {
            fileDownLoad.songStatus = ERROR
            fileDownLoad.downloadStatus = PAUSE
            fileDownLoad.errorResource = ErrorResource(error.message)
            _listSongDownload.value = fileDownLoad
        }
    }

    private fun downloadComplete(
        fileDownLoad: DownloadData
    ) {
        appExecutors.mainThread().execute {
            fileDownLoad.songStatus = PLAY
            fileDownLoad.downloadStatus = NONE
            _listSongDownload.value = fileDownLoad
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
            fileDownLoad.songStatus = NONE_STATUS
            fileDownLoad.downloadStatus = NONE
            fileDownLoad.progress = 0
            Logger.debug("cancelComplete : fileDownLoad - $fileDownLoad")
            removeTaskDownload(fileDownLoad)
            _listSongDownload.value = fileDownLoad
        }
    }
}