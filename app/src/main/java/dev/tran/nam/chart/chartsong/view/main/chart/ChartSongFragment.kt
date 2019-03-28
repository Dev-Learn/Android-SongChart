package dev.tran.nam.chart.chartsong.view.main.chart

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
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

    override fun initViewModel(factory: ViewModelProvider.Factory?) {
        mViewModel = ViewModelProviders.of(this, factory).get(ChartSongViewModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.fragment_chart_week
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewDataBinding?.viewModel = mViewModel

        val adapterSongWeek = SongWeekAdapter(appExecutors, dataBindingComponent, { item, position ->
            run {
                when (item.songStatus) {
                    DOWNLOAD -> {
                        item.songStatus = CANCEL_DOWNLOAD
                        item.downloadStatus = PAUSE
                        mViewModel?.downloadSong(item)
                    }
                    CANCEL_DOWNLOAD -> {
                        item.songStatus = DOWNLOAD
                        item.downloadStatus = NONE
                    }
                    PLAY -> {
                        item.songStatus = STOP
                    }
                    STOP -> {
                        item.songStatus = PLAY
                    }
                    else -> {}
                }
            }
        }, { item, position ->
            run {
                when (item.downloadStatus) {
                    PAUSE -> {
                        item.downloadStatus = RESUME
                    }
                    RESUME -> {
                        item.downloadStatus = PAUSE
                    }
                    NONE -> {}
                }
            }
        })
        mViewDataBinding?.rvSongWeek?.adapter = adapterSongWeek

        val adapterWeekChart = WeekChartAdapter(appExecutors, dataBindingComponent) { it, position ->
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
            Logger.debug(it)
            it?.run {
                for (item in this){
                    if (item.errorResource != null){
                        Toast.makeText(requireContext(),item.errorResource?.message,Toast.LENGTH_SHORT).show()
                    }
                    val position = adapterSongWeek.currentList.indexOf(item)
                    if (position != -1){
                        Logger.debug(position)
                        adapterSongWeek.updateItem(position,item)
                    }
                }
            }
        })

        if (savedInstanceState == null){
            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "ChartSong"
            Logger.debug(path)
            val folder = File(path)
            if (!folder.exists()){
                val success = folder.mkdirs()
                print(success)
            }
            mViewModel?.getData(pathFolder = folder.absolutePath)
        }
    }
}
