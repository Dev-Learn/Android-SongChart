package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.api.IApi
import nam.tran.data.controller.IDownloadController
import nam.tran.data.controller.IPlayerController
import nam.tran.data.model.DownloadData
import nam.tran.data.model.DownloadStatus
import nam.tran.data.model.Song
import nam.tran.data.model.SongStatus
import nam.tran.data.model.core.state.ErrorResource
import nam.tran.data.model.core.state.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class SingerUseCase @Inject internal constructor(
    private val iApi: IApi, private val iPlayerController: IPlayerController
    , private val iDownloadController: IDownloadController
) : DownloadAndPlayUseCase(iPlayerController, iDownloadController), ISingerUseCase {


    private val _listSongSinger = MutableLiveData<Resource<List<Song>>>()
    override val listSongSinger: LiveData<Resource<List<Song>>>
        get() = _listSongSinger


    override fun getData(idSinger: Int, pathFolder: String?) {
        pathFolder?.run {
            folderPath = this
        }

        _listSongSinger.value = Resource.loading()
        iApi.getListSongSinger(idSinger).enqueue(object : Callback<List<Song>> {
            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                _listSongSinger.value = Resource.error(ErrorResource(t.message, t.hashCode()), retry = {
                    getData(idSinger, pathFolder)
                })
            }

            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.run {
                        val listItem = getListIdPause()
                        val idPause = pauseId()
                        forEach {
                            val file = File(folderPath.plus("/").plus(it.id).plus(".mp3"))
                            print(file)
                            if (it.statusDownload(file) == 2)
                                updatePauseDownload(it)
                            val downloadItem = DownloadData(it.id)
                            if (iDownloadController.checkItemNotUpdateUI(it.id) && it.statusDownload(file) == 1) {
                                it.songStatus = SongStatus.PLAY
                                it.downloadStatus = DownloadStatus.NONE
                            }
                            if (iPlayerController.checkPlayerNotUpdateUI(it.id)) {
                                it.songStatus = SongStatus.PLAY
                                it.downloadStatus = DownloadStatus.NONE
                            }
                            if (listItem.contains(downloadItem)) {
                                val itemChild = listItem[listItem.indexOf(downloadItem)]
                                it.songStatus = itemChild.songStatus
                                it.downloadStatus = itemChild.downloadStatus
                                it.errorResource = itemChild.errorResource
                                it.progressDownload = itemChild.progress
                            }
                            if (idPause != -1 && it.id == idPause) {
                                it.songStatus = SongStatus.PAUSE_SONG
                                it.downloadStatus = DownloadStatus.NONE
                            }
                        }
                        _listSongSinger.value = Resource.success(body)
                    }

                } else {
                    _listSongSinger.value = Resource.error(
                        ErrorResource(
                            JSONObject(response.errorBody()?.string()).getString("message"),
                            response.code()
                        ), retry = {
                            getData(idSinger, pathFolder)
                        }
                    )
                }
            }

        })
    }
}