package nam.tran.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import nam.tran.data.BR

data class WeekChart(val id: Int, val name: String, var listWeekSong: List<WeekSong>, var _isChoose : Boolean = false) : BaseObservable() {

    var isChoose: Boolean
        @Bindable get() = _isChoose
        set(value) {
            _isChoose = value
            notifyPropertyChanged(BR.choose)
        }
}