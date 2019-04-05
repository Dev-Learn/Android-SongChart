package dev.tran.nam.chart.chartsong.view.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.AdapterSongWeekBinding
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.Singer
import nam.tran.data.model.Song
import nam.tran.data.model.core.state.ErrorResource
import tran.nam.common.DataBoundListAdapter
import tran.nam.common.DataBoundViewHolder

class SongAdapter (
    appExecutors: AppExecutors, private val dataBindingComponent: DataBindingComponent
    , val songStatusClick: (Song, Int) -> Unit, val downloadStatusClick: (Song, Int) -> Unit
    , val stopMusicClick: (Song, Int) -> Unit, val singerClick: ((Singer?) -> Unit)? = null
) : DataBoundListAdapter<Song, AdapterSongWeekBinding>(appExecutors, object : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
         val isSame = if(oldItem.idWeek != null && newItem.idWeek != null)
             oldItem.idWeek == newItem.idWeek else true
        return isSame && oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.songStatus == newItem.songStatus && oldItem.downloadStatus == newItem.downloadStatus
                && oldItem.position == newItem.position
    }

}) {
    override fun createBinding(parent: ViewGroup): AdapterSongWeekBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_song_week,
            parent,
            false,
            dataBindingComponent
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<AdapterSongWeekBinding> {
        val hoder = super.onCreateViewHolder(parent, viewType)
        val binding = hoder.binding
        binding.ivStatusSong.setOnClickListener {
            binding.song?.let {
                songStatusClick.invoke(it, hoder.adapterPosition)
            }
        }
        binding.ivStatusDownload.setOnClickListener {
            binding.song?.let {
                downloadStatusClick.invoke(it, hoder.adapterPosition)
            }
        }
        binding.progressDownload.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }
        binding.ivCloseSong.setOnClickListener {
            binding.song?.let {
                stopMusicClick.invoke(it, hoder.adapterPosition)
            }
        }
        binding.tvSinger.setOnClickListener {
            binding.song?.let {
                singerClick?.invoke(it.singer)
            }
        }
        return hoder
    }

    override fun bind(binding: AdapterSongWeekBinding, item: Song) {
        binding.song = item
    }

    fun updateItemDownload(index: Int, progressDownload : Int, songStatus : Int, downloadStatus : Int,errorResource: ErrorResource?) {
        val data = getItem(index)
        data.progressDownload = progressDownload
        data.downloadStatus = downloadStatus
        data.songStatus = songStatus
        errorResource?.run {
            data.errorResource = this
        }
    }

    fun updateItemPlay(index: Int, songStatus : Int) {
        val data = getItem(index)
        data.songStatus = songStatus
    }

    fun getPosition(idSong : Int) : Int{
        currentList.forEachIndexed { index, song ->
            if (song.id == idSong)
                return index
        }
        return -1
    }
}