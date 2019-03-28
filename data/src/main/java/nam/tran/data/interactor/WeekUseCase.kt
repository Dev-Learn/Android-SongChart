package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.Logger
import nam.tran.data.api.IApi
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.DownloadStatus.NONE
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
import java.util.*
import javax.inject.Inject


class WeekUseCase @Inject internal constructor(private val appExecutors: AppExecutors, private val iApi: IApi) :
    IWeekUseCase {

    private var isCancle = false
    private var currentPosition = 0
    private lateinit var folderPath: String

    private val _listWeekChart = MutableLiveData<Resource<List<WeekChart>>>()
    override val listWeekChart: LiveData<Resource<List<WeekChart>>>
        get() = _listWeekChart

    private val _listSongWeek = MediatorLiveData<Resource<List<WeekSong>>>()
    override val listSongWeek: LiveData<Resource<List<WeekSong>>>
        get() = _listSongWeek

    private val _listSongDownload = MutableLiveData<Vector<WeekSong>>()
    override val listSongDownload: LiveData<Vector<WeekSong>>
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
                            val file = File(folderPath.plus("/").plus(song.song.name).plus(".mp3"))
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

    override fun getDataExist(position: Int) {
        isCancle = true
        val data = _listWeekChart.value
        data?.data?.run {
            val listSongWeek = this[position].listWeekSong
            _listSongWeek.value = Resource.success(listSongWeek)
        }
    }

    override fun downloadMusic(song: WeekSong) {
        var data = listSongDownload.value
        if (data == null){
            data = Vector()
        }
        data.add(song)
        appExecutors.networkIO().execute {
            val path = folderPath.plus("/").plus(song.song.name).plus(".mp3")
            try {
                val url = URL(song.song.link_local)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                val fileLength = connection.contentLength

                // download the file
                val input = connection.inputStream
                val output = FileOutputStream(path)

                val buffer = ByteArray(1024)
                var total: Long = 0
                var length: Int

                while (input.read(buffer, 0, 1024).let { length = it; length > 0 }) {
                    total += length
                    if (fileLength > 0) {
                        song.progressDownload = (total * 100 / fileLength).toInt()
                        _listSongDownload.postValue(data)
                    }
                    output.write(buffer, 0, length)
                }

                output.close()
                input.close()

                song._songStatus = PLAY
                song._downloadStatus = NONE
                _listSongDownload.postValue(data)

            } catch (e: IOException) {
                Logger.debug(e)
                song._songStatus = DOWNLOAD
                song._downloadStatus = NONE
                song.errorResource = ErrorResource(e.message)
                Logger.debug(data)
                _listSongDownload.postValue(data)
                val file = File(path)
                if (file.exists()){
                    val result = file.delete()
                    Logger.debug(result)
                }
            }

        }
    }
}