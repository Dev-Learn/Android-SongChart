package dev.tran.nam.chart.chartsong.view.main.chart

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.tbruyelle.rxpermissions2.RxPermissions
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.FragmentChartWeekBinding
import dev.tran.nam.chart.chartsong.view.main.SongAdapter
import dev.tran.nam.chart.chartsong.view.main.chart.viewmodel.ChartSongViewModel
import nam.tran.data.Logger
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.SongStatus.NONE_STATUS
import nam.tran.data.model.SongStatus.PLAY
import tran.nam.core.biding.FragmentDataBindingComponent
import tran.nam.core.view.mvvm.BaseFragmentVM
import java.io.File
import javax.inject.Inject


class ChartSongFragment : BaseFragmentVM<FragmentChartWeekBinding, ChartSongViewModel>() {

    @Inject
    lateinit var appExecutors: AppExecutors

    private val dataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var adapterWeekChart: WeekChartAdapter

    private lateinit var folder : File

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
        folder = File(path)
        if (!folder.exists()) {
            val success = folder.mkdirs()
            print(success)
        }

        val adapterSongWeek =
            SongAdapter(appExecutors, dataBindingComponent, { item, _ ->
                run {
                    if (item._songStatus == NONE_STATUS) {
                        RxPermissions(this@ChartSongFragment)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe {
                                if (it) {
                                    mViewModel?.songClick(item, folder.absolutePath)
                                } else {
                                    Logger.debug("All permissions were NOT granted.")
                                    val alertDialogBuilder = AlertDialog.Builder(activity)
                                    alertDialogBuilder.setMessage("You must allow permission to download")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok") { dialog, _ ->
                                            dialog.dismiss()
                                            val intent = Intent()
                                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                                            intent.data = uri
                                            startActivity(intent)
                                        }.setNegativeButton("Cancel") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                    alertDialogBuilder.show()
                                }
                            }
                    } else {
                        mViewModel?.songClick(item, folder.absolutePath)
                    }
                }
            }, { item, _ ->
                run {
                    mViewModel?.downloadStatusClick(item)
                }
            }, { item, _ ->
                run {
                    mViewModel?.stopSong(item.id)
                }
            }, {
                it?.run {
                    val bundle = Bundle()
                    bundle.putSerializable("singer", this)
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_chartSongFragment_to_singerFragment, bundle)
                }
            })
        mViewDataBinding?.rvSongWeek?.adapter = adapterSongWeek

        adapterWeekChart = WeekChartAdapter(
            appExecutors,
            dataBindingComponent
        ) { it, position ->
            mViewModel?.mPosition = position
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
                    }, 300)
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
                } else {
                    if (songStatus == PLAY) {
                        mViewModel?.updateSongDownloadCompleteNotUi(id)
                    }
                }
            }
        })

        mViewModel?.resultPlay?.observe(viewLifecycleOwner, Observer {
            it?.run {
                mViewModel?.updateNotification(this)
                if (idOld != null) {
                    val index = adapterSongWeek.getPosition(idOld!!)
                    if (index != -1) {
                        adapterSongWeek.updateItemPlay(index, PLAY)
                    } else {
                        mViewModel?.updateSongStatus(this)
                    }
                }
                val index = adapterSongWeek.getPosition(id)
                if (index != -1) {
                    adapterSongWeek.updateItemPlay(index, songStatus)
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null && mViewModel?.mPosition == -1) {
            mViewModel?.getData(pathFolder = folder.absolutePath)
        }else{
            savedInstanceState?.getInt("weekSelect", 0).run {
                adapterWeekChart.position = mViewModel?.mPosition ?: 0
            }
        }
    }
}
