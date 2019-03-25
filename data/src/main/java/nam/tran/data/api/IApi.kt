package nam.tran.data.api

import nam.tran.data.model.Singer
import nam.tran.data.model.Song
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IApi{

    @GET("/getChartMusic")
    fun getCharMusic() : Call<List<WeekChart>>

    @GET("/getSongWeek")
    fun getListSongWeek(@Query("weekId") id : Int) : Call<List<WeekSong>>

    @GET("/getSinger")
    fun getListSinger() : Call<List<Singer>>

    @GET("/getSongSinger")
    fun getListSongSinger(@Query("singerId") singerId : Int) : Call<List<Song>>
}
