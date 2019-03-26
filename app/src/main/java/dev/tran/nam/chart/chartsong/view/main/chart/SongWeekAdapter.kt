package dev.tran.nam.chart.chartsong.view.main.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.AdapterSongWeekBinding
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.WeekChart
import nam.tran.data.model.WeekSong
import tran.nam.common.DataBoundListAdapter

class SongWeekAdapter constructor(appExecutors: AppExecutors, private val dataBindingComponent: DataBindingComponent)
    : DataBoundListAdapter<WeekSong,AdapterSongWeekBinding>(appExecutors,object : DiffUtil.ItemCallback<WeekSong>(){
    override fun areItemsTheSame(oldItem: WeekSong, newItem: WeekSong): Boolean {
        return oldItem.song == newItem.song
    }

    override fun areContentsTheSame(oldItem: WeekSong, newItem: WeekSong): Boolean {
        return oldItem.position == newItem.position && oldItem.hierarchical == newItem.hierarchical
    }

}) {
    override fun createBinding(parent: ViewGroup): AdapterSongWeekBinding {
        val binding : AdapterSongWeekBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_song_week,
            parent,
            false,
            dataBindingComponent
        )
        return binding
    }

    override fun bind(binding: AdapterSongWeekBinding, item: WeekSong) {
        binding.song = item
    }
}