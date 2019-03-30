package nam.tran.data.model

import nam.tran.data.model.SongStatus.PLAYING

data class SongPlayerData(var id: Int? = null, @SongStatus var songStatus: Int = PLAYING, var progress: Int = 0)