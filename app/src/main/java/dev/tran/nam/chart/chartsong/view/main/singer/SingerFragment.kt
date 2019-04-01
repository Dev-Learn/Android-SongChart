package dev.tran.nam.chart.chartsong.view.main.singer

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
import com.tbruyelle.rxpermissions2.RxPermissions
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.FragmentSingerBinding
import dev.tran.nam.chart.chartsong.view.main.SongAdapter
import dev.tran.nam.chart.chartsong.view.main.singer.viewmodel.SingerViewModel
import kotlinx.android.synthetic.main.fragment_singer.*
import nam.tran.data.Logger
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.Singer
import nam.tran.data.model.SongStatus
import tran.nam.core.biding.FragmentDataBindingComponent
import tran.nam.core.view.mvvm.BaseFragmentVM
import java.io.File
import javax.inject.Inject

class SingerFragment : BaseFragmentVM<FragmentSingerBinding, SingerViewModel>() {

    @Inject
    lateinit var appExecutors: AppExecutors

    private val dataBindingComponent = FragmentDataBindingComponent(this)
    private val folderPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "ChartSong"

    override fun initViewModel(factory: ViewModelProvider.Factory?) {
        mViewModel = ViewModelProviders.of(this, factory).get(SingerViewModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.fragment_singer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewDataBinding?.viewModel = mViewModel

        arguments?.run {
            val singer = getSerializable("singer") as Singer
            tv_singer.text = singer.name
            mViewModel?.getData(
                singer.id, folderPath
            )
        }

        val adapterSongWeek =
            SongAdapter(appExecutors, dataBindingComponent, { item, _ ->
                run {
                    if (item._songStatus == SongStatus.NONE_STATUS) {
                        RxPermissions(this@SingerFragment)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe {
                                if (it) {
                                    mViewModel?.songClick(item, folderPath)
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
                        mViewModel?.songClick(item, folderPath)
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
            })
        mViewDataBinding?.rvSongWeek?.adapter = adapterSongWeek

        mViewModel?.results?.observe(viewLifecycleOwner, Observer {
            it?.run {
                if (it.isSuccess()) {
                    adapterSongWeek.submitList(it.data)
                }
                mViewDataBinding?.viewModel = mViewModel
            }
        })

        mViewModel?.resultListDownload?.observe(viewLifecycleOwner, Observer {
            it?.run {
                val index = adapterSongWeek.getPosition(id)
                if (index != -1) {
                    adapterSongWeek.updateItemDownload(index, progress, songStatus, downloadStatus)
                } else {
                    if (songStatus == SongStatus.PLAY) {
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
                        adapterSongWeek.updateItemPlay(index, SongStatus.PLAY)
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
}
