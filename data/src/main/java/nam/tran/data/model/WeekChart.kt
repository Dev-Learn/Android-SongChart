package nam.tran.data.model

data class WeekChart(val id : Int, val name : String, var listWeekSong: List<WeekSong>, var isChoose : Boolean = false)