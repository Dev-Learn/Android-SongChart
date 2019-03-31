package dev.tran.nam.chart.chartsong.view.main.chart

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.controller.NotificationController
import dev.tran.nam.chart.chartsong.databinding.FragmentChartWeekBinding
import dev.tran.nam.chart.chartsong.view.main.chart.viewmodel.ChartSongViewModel
import nam.tran.data.Logger
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.DownloadStatus.*
import nam.tran.data.model.SongStatus.*
import tran.nam.core.biding.FragmentDataBindingComponent
import tran.nam.core.view.mvvm.BaseFragmentVM
import java.io.File
import javax.inject.Inject



class ChartSongFragment : BaseFragmentVM<FragmentChartWeekBinding, ChartSongViewModel>() {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var mNotificationController: NotificationController

    private val dataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var adapterWeekChart: WeekChartAdapter

    override fun initViewModel(factory: ViewModelProvider.Factory?) {
        mViewModel = ViewModelProviders.of(this, factory).get(ChartSongViewModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.fragment_chart_week
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewDataBinding?.viewModel = mViewModel

        val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "ChartSong"
        Logger.debug(path)
        val folder = File(path)
        if (!folder.exists()){
            val success = folder.mkdirs()
            print(success)
        }

        val adapterSongWeek = SongWeekAdapter(appExecutors, dataBindingComponent, { item, position ->
            run {
                when (item.songStatus) {
                    NONE_STATUS -> {
                        item.songStatus = DOWNLOADING
                        item.downloadStatus = RUNNING
                        mViewModel?.downloadSong(item)
                    }
                    DOWNLOADING, ERROR -> {
                        mViewModel?.updateStatus(item.song.id, CANCEL_DOWNLOAD)
                    }
                    PLAY -> {
                        mViewModel?.playSong(item.song.name,item.song.id,folder.absolutePath)
                    }
                    PLAYING -> {
                        mViewModel?.pauseSong()
                    }
                    PAUSE_SONG -> {
                        mViewModel?.playSong(item.song.name,item.song.id,folder.absolutePath)
                    }
                    else -> {}
                }
            }
        }, { item, position ->
            run {
                when (item.downloadStatus) {
                    RUNNING -> {
                        item.downloadStatus = PAUSE
                        mViewModel?.updateStatus(item.song.id,PAUSE,true)
                    }
                    PAUSE -> {
                        item.downloadStatus = RUNNING
                        mViewModel?.downloadSong(item, true)
                    }
                    NONE -> {}
                    else -> {}
                }
            }
        }, { item, position ->
            run {
                mViewModel?.stopSong(item.song.id)
            }
        })
        mViewDataBinding?.rvSongWeek?.adapter = adapterSongWeek

        adapterWeekChart = WeekChartAdapter(
            appExecutors,
            dataBindingComponent,
            savedInstanceState?.getInt("weekSelect", 0) ?: 0
        ) { it, position ->
            if (!it.listWeekSong.isNullOrEmpty()) {
                mViewModel?.getDataExist(position)
            } else {
                mViewModel?.getData(position)
            }
        }
        mViewDataBinding?.rvWeek?.adapter = adapterWeekChart

        mViewModel?.results?.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess()) {
                it.data?.run {
                    adapterWeekChart.submitList(this)
                }
                mViewDataBinding?.line?.visibility = View.VISIBLE
            }
            mViewDataBinding?.viewModel = mViewModel
        })

        mViewModel?.resultChild?.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isSuccess()) {
                    adapterSongWeek.submitList(it.data)
                    mViewDataBinding?.rvSongWeek?.postDelayed({
                        mViewDataBinding?.rvSongWeek?.visibility = View.VISIBLE
                    }, 200)
                } else {
                    mViewDataBinding?.rvSongWeek?.visibility = View.INVISIBLE
                }
            }
            mViewDataBinding?.viewModel = mViewModel
        })

        mViewModel?.resultListDownload?.observe(viewLifecycleOwner, Observer {
            it?.run {
                val index = adapterSongWeek.getPosition(id)
                if (index != -1) {
                    adapterSongWeek.updateItemDownload(index, progress, songStatus, downloadStatus)
                }
            }
        })

        mViewModel?.resultSongPlay?.observe(viewLifecycleOwner, Observer {
            it?.run {
                if (songStatus == PLAY)
                    mNotificationController.clearNotification(id)
                else
                    mNotificationController.updatePlayerSong(id,name,progress,total)
                if (idOld != null) {
                    mNotificationController.clearNotification(idOld!!)
                    val index = adapterSongWeek.getPosition(idOld!!)
                    if (index != -1) {
                        adapterSongWeek.updateItemPlay(index, PLAY)
                    }
                }
                val index = adapterSongWeek.getPosition(id)
                if (index != -1) {
                    adapterSongWeek.updateItemPlay(index, songStatus)
                }
            }
        })

        if (savedInstanceState == null){
            mViewModel?.getData(pathFolder = folder.absolutePath)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("weekSelect", adapterWeekChart.getPositionSelect())
    }
}
