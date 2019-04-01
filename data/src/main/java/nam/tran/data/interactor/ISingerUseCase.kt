package nam.tran.data.interactor

import androidx.lifecycle.LiveData
import nam.tran.data.model.Song
import nam.tran.data.model.core.state.Resource

interface ISingerUseCase : IDownloadAndPlayUseCase {

    val listSongSinger: LiveData<Resource<List<Song>>>
    fun getData(idSinger : Int,pathFolder : String? = null)
}