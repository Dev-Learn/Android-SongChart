package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.api.IApi
import nam.tran.data.controller.IDownloadController
import nam.tran.data.controller.IPlayerController
import nam.tran.data.model.Song
import nam.tran.data.model.core.state.ErrorResource
import nam.tran.data.model.core.state.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SingerUseCase @Inject internal constructor(
    private val iApi: IApi, iPlayerController: IPlayerController
    , iDownloadController: IDownloadController
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
                    _listSongSinger.value = Resource.success(response.body())
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