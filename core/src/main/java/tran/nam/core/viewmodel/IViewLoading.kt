package tran.nam.core.viewmodel

interface IViewLoading : IViewLifecycle {

    fun showDialogLoading()

    fun hideDialogLoading()

    fun onShowDialogError(message: String?, codeError: Int?)
}
