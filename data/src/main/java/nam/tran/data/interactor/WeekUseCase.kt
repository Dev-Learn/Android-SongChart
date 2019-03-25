package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.api.IApi
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import nam.tran.data.model.core.state.ErrorResource
import nam.tran.data.model.core.state.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class WeekUseCase @Inject internal constructor(private val appExecutors: AppExecutors,private val iApi: IApi) : IWeekUseCase {

    private val _listWeekChart = MutableLiveData<Resource<List<WeekChart>>>()
    override val listWeekChart: LiveData<Resource<List<WeekChart>>>
        get() = _listWeekChart

    override fun getData(position: Int?) {
        if (position == null){
            _listWeekChart.value = Resource.loading()
            iApi.getCharMusic().enqueue(object : Callback<List<WeekChart>>{
                override fun onFailure(call: Call<List<WeekChart>>, t: Throwable) {
                    _listWeekChart.value = Resource.error(ErrorResource(t.message,t.hashCode()))
                }

                override fun onResponse(call: Call<List<WeekChart>>, response: Response<List<WeekChart>>) {
                    if (response.isSuccessful){
                        val data = response.body()
                        if (data?.isNotEmpty() == true){
                            getSongWeek(0, data)
                        }
                    }else{
                        _listWeekChart.value = Resource.error(ErrorResource(
                                JSONObject(response.errorBody()?.string()).getString("message"),
                                response.code()))
                    }
                }
            })
        }else{
            listWeekChart.value?.data?.run {
                getSongWeek(position,this)
            }

        }
    }

    private fun getSongWeek(postion: Int, listDataWeekChart: List<WeekChart>){
        iApi.getListSongWeek(listDataWeekChart[postion].id).enqueue(object : Callback<List<WeekSong>>{
            override fun onFailure(call: Call<List<WeekSong>>, t: Throwable) {
                _listWeekChart.value = Resource.error(ErrorResource(t.message,t.hashCode()))
            }

            override fun onResponse(call: Call<List<WeekSong>>, response: Response<List<WeekSong>>) {
                if (response.isSuccessful){
                    val listWeekSong = response.body()
                    listWeekSong?.run {
                        listDataWeekChart[postion].listWeekSong = this
                        _listWeekChart.value = Resource.success(listDataWeekChart)
                    }
                }else{
                    _listWeekChart.value = Resource.error(ErrorResource(
                        JSONObject(response.errorBody()?.string()).getString("message"),
                        response.code()))
                }
            }

        })
    }
}