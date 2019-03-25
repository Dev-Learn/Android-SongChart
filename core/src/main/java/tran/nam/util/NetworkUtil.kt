package tran.nam.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

class NetworkUtil {

    companion object {
        @SuppressLint("MissingPermission")
        fun isConnected(context: Context): Boolean {
            val cm: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm?.activeNetworkInfo
            return null != activeNetwork && activeNetwork.isConnected
        }
    }

}