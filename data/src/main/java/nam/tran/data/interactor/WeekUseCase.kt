package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.api.IApi
import nam.tran.data.controller.IDownloadController
import nam.tran.data.controller.IPlayerController
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.DownloadData
import nam.tran.data.model.DownloadStatus.NONE
import nam.tran.data.model.Song
import nam.tran.data.model.SongStatus.PAUSE_SONG
import nam.tran.data.model.SongStatus.PLAY
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.ErrorResource
import nam.tran.data.model.core.state.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject


class WeekUseCase @Inject internal constructor(
    private val appExecutors: AppExecutors, private val iApi: IApi
    , private val iPlayerController: IPlayerController
    , private val iDownloadController: IDownloadController
) : DownloadAndPlayUseCase(iPlayerController, iDownloadController), IWeekUseCase {

    private var isCancle = false
    private var currentPosition = 0


    private val _listWeekChart = MutableLiveData<Resource<List<WeekChart>>>()
    override val listWeekChart: LiveData<Resource<List<WeekChart>>>
        get() = _listWeekChart

    private val _listSongWeek = MediatorLiveData<Resource<List<Song>>>()
    override val listSongWeek: LiveData<Resource<List<Song>>>
        get() = _listSongWeek

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
                        val listSong = mutableListOf<Song>()
                        for (song in this) {
                            val file = File(folderPath.plus("/").plus(song.song.id).plus(".mp3"))
                            print(file)
                            if (file.exists()) {
                                song.song.songStatus = PLAY
                            }
                            listSong.add(song.song)
                        }
                        listDataWeekChart[position].listWeekSong = listSong
                        _listWeekChart.value = Resource.success(listDataWeekChart)
                        if (!isCancle && currentPosition == position)
                            _listSongWeek.value = Resource.success(listSong)
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
        _listSongWeek.value = Resource.loading()
        appExecutors.diskIO().execute {
            val data = _listWeekChart.value
            data?.data?.run {
                val listSongWeek = this[position].listWeekSong
                val listItem = getListIdPause()
                val idPause = pauseId()
                for (item in listSongWeek) {
                    val downloadItem = DownloadData(item.id)
                    if (iDownloadController.checkItemNotUpdateUI(item.id)) {
                        item.songStatus = PLAY
                        item.downloadStatus = NONE
                    }
                    if (iPlayerController.checkPlayerNotUpdateUI(item.id)) {
                        item.songStatus = PLAY
                        item.downloadStatus = NONE
                    }
                    if (listItem.contains(downloadItem)) {
                        val itemChild = listItem[listItem.indexOf(downloadItem)]
                        item.songStatus = itemChild.songStatus
                        item.downloadStatus = itemChild.downloadStatus
                        item.errorResource = itemChild.errorResource
                        item.progressDownload = itemChild.progress
                    }
                    if (idPause != -1 && item.id == idPause) {
                        item.songStatus = PAUSE_SONG
                        item.downloadStatus = NONE
                    }
                }
                _listSongWeek.postValue(Resource.success(listSongWeek))
            }
        }
    }

}