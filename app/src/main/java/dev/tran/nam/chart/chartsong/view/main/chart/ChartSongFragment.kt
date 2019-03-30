package dev.tran.nam.chart.chartsong.view.main.chart

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dev.tran.nam.chart.chartsong.R
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
                    PAUSE -> {

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
                forEach {
                    if (it.songStatus == CANCEL_DOWNLOAD) {
                        mViewModel?.removeTaskDownload(it)
                    }
                    val index = adapterSongWeek.getPosition(it.id)
                    if (index != -1) {
                        adapterSongWeek.updateItem(index, it.progress, it.songStatus, it.downloadStatus)
                    }
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
