package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nam.tran.data.api.IApi
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.WeekChart
import nam.tran.data.model.core.state.ErrorResource
import nam.tran.data.model.core.state.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class WeekUseCase @Inject internal constructor(private val appExecutors: AppExecutors,private val iApi: IApi) : IWeekUseCase {

    private val _listWeekChart = MutableLiveData<Resource<WeekChart>>()
    override val listWeekChart: LiveData<Resource<WeekChart>>
        get() = _listWeekChart

    override fun getData(weekId: Int?) {
        if (weekId == null){
            _listWeekChart.value = Resource.loading()
            iApi.getCharMusic().enqueue(object : Callback<List<WeekChart>>{
                override fun onFailure(call: Call<List<WeekChart>>, t: Throwable) {
                    _listWeekChart.value = Resource.error(ErrorResource(t.message,t.hashCode()))
                }

                override fun onResponse(call: Call<List<WeekChart>>, response: Response<List<WeekChart>>) {
                    if (response.isSuccessful){

                    }else{
                        _listWeekChart.value = Resource.error(ErrorResource())
                    }
                }
            })
        }
    }
}