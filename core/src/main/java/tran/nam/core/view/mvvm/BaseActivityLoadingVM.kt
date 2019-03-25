package tran.nam.core.view.mvvm

import androidx.databinding.ViewDataBinding
import tran.nam.core.viewmodel.BaseActivityViewModel
import tran.nam.core.viewmodel.IViewLoading

abstract class BaseActivityLoadingVM<V : ViewDataBinding, VM : BaseActivityViewModel> : BaseActivityVM<V, VM>(), IViewLoading {

    override fun showDialogLoading() {
        showLoadingDialog()
    }

    override fun hideDialogLoading() {
        hideLoadingDialog()
    }

    override fun onShowDialogError(message: String?, codeError: Int?) {
        hideLoadingDialog()
    }
}