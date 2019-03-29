package dev.tran.nam.chart.chartsong.view.main.chart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.AdapterSongWeekBinding
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.WeekSong
import tran.nam.common.DataBoundListAdapter
import tran.nam.common.DataBoundViewHolder

class SongWeekAdapter constructor(
    appExecutors: AppExecutors, private val dataBindingComponent: DataBindingComponent
    , val songStatusClick: (WeekSong, Int) -> Unit, val downloadStatusClick: (WeekSong, Int) -> Unit
) : DataBoundListAdapter<WeekSong, AdapterSongWeekBinding>(appExecutors, object : DiffUtil.ItemCallback<WeekSong>() {
    override fun areItemsTheSame(oldItem: WeekSong, newItem: WeekSong): Boolean {
        return oldItem.song == newItem.song
    }

    override fun areContentsTheSame(oldItem: WeekSong, newItem: WeekSong): Boolean {
        return oldItem.position == newItem.position && oldItem.hierarchical == newItem.hierarchical
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
        binding.progressDownload.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
        return hoder
    }

    override fun bind(binding: AdapterSongWeekBinding, item: WeekSong) {
        binding.song = item
    }

    fun updateItem(index: Int, progressDownload : Int,songStatus : Int,downloadStatus : Int) {
        val data = getItem(index)
        data.progressDownload = progressDownload
        data.downloadStatus = downloadStatus
        data.songStatus = songStatus
    }

    fun getPosition(idSong : Int) : Int{
        currentList.forEachIndexed { index, weekSong ->
            if (weekSong.song.id == idSong)
                return index
        }
        return -1
    }
}