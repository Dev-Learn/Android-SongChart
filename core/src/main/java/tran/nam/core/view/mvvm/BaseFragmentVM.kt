/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package tran.nam.core.view.mvvm

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import tran.nam.common.autoCleared
import tran.nam.core.R
import tran.nam.core.view.BaseFragmentInjection
import tran.nam.core.viewmodel.BaseFragmentViewModel
import tran.nam.core.viewmodel.IViewLoading
import javax.inject.Inject


abstract class BaseFragmentVM<V : ViewDataBinding, VM : BaseFragmentViewModel> : BaseFragmentInjection(), IViewLoading {

    /**
     * MVVM ViewModel ViewModelProvider.Factory
     */
    var mViewModelFactory: ViewModelProvider.Factory? = null
        @Inject set

    var mViewModel: VM? = null

    var mViewDataBinding: V? = null

    var binding by autoCleared<V>()

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    abstract fun initViewModel(factory: ViewModelProvider.Factory?)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel(mViewModelFactory)
        mViewModel?.onAttach(this)
    }

    override fun initLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        binding = mViewDataBinding as V
        return binding.root
    }

    open fun navigation(navigator: (() -> Unit)) {
        handler = Handler()
        runnable = Runnable {
            navigator()
        }
        handler?.postDelayed(runnable, 300)
    }

    override fun onDestroy() {
        this.mViewDataBinding?.unbind()
        handler?.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun showDialogLoading() {
        showLoadingDialog()
    }

    override fun hideDialogLoading() {
        hideLoadingDialog()
    }

    override fun onShowDialogError(message: String?, codeError: Int?) {
        hideDialogLoading()
        val alertDialog = AlertDialog.Builder(context!!)
        alertDialog.setTitle(R.string.title_error)
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton(
            getString(R.string.text_ok)
        ) { dialog, _ -> dialog.dismiss() }

        alertDialog.create().show()
    }
}
