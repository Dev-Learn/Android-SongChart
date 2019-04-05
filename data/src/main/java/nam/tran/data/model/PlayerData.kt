package nam.tran.data.model

import nam.tran.data.model.SongStatus.PLAYING
import nam.tran.data.model.core.state.ErrorResource

data class PlayerData(
    var id: Int,
    var name: String = "", @SongStatus var songStatus: Int = PLAYING
    , var progress: Int = 0, var total: Int = 0, var idOld: Int? = null, var errorResource: ErrorResource? = null
)